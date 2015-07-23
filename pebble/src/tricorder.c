#include <pebble.h>
#include "tricorder.h"
#include "crc32.h"

#define DEFAULT_TAG 42
#define DEFAULT_DELAY_INTERVAL 500

static void prv_delay_timer_callback(void *data);

typedef struct Tricorder {
  TricorderData *data;
  DataLoggingSessionRef logging_session;
  uint32_t tag;
  AppTimer *delay_timer;
  uint32_t delay_interval;
  TricorderUpdateHandler update_handler;
} Tricorder;

Tricorder* tricorder_create(void) {
  Tricorder *tricorder = malloc(sizeof(Tricorder));
  if (!tricorder) {
    APP_LOG(APP_LOG_LEVEL_ERROR, "failed to allocate memory for tricorder");
    return NULL;
  }

  *tricorder = (Tricorder) {
    .tag = DEFAULT_TAG,
    .delay_interval = DEFAULT_DELAY_INTERVAL,
  };

  tricorder->data = malloc(sizeof(TricorderData));
  if (!tricorder->data) {
    APP_LOG(APP_LOG_LEVEL_ERROR, "failed to allocate memory for tricorder data");
    free(tricorder);
    return NULL;
  }

  *tricorder->data = (TricorderData) {0};

  return tricorder;
}

void tricorder_destroy(Tricorder *tricorder) {
  if (!tricorder) return;

  app_timer_cancel(tricorder->delay_timer);
  if (tricorder->logging_session) {
    data_logging_finish(tricorder->logging_session);
  }
  free(tricorder->data);
  free(tricorder);
}

void tricorder_start_logging(Tricorder *tricorder) {
  if (!tricorder) return;
  if (tricorder->logging_session) return;

  tricorder_reset_data(tricorder);

  tricorder->logging_session = data_logging_create(tricorder->tag,
                                                   DATA_LOGGING_BYTE_ARRAY,
                                                   sizeof(TricorderData), true);
  if (!tricorder->logging_session) {
    APP_LOG(APP_LOG_LEVEL_ERROR, "failed to create data logging session");
    return;
  }

  tricorder->delay_timer = app_timer_register(0, prv_delay_timer_callback, tricorder);
}

void tricorder_stop_logging(Tricorder *tricorder) {
  if (!tricorder) return;
  if (!tricorder->logging_session) return;

  app_timer_cancel(tricorder->delay_timer);
  data_logging_finish(tricorder->logging_session);
  tricorder->logging_session = NULL;
}

bool tricorder_is_logging(Tricorder *tricorder) {
  if (!tricorder) return false;

  return tricorder->logging_session != NULL;
}

void tricorder_reset_data(Tricorder *tricorder) {
  if (!tricorder) return;

  *tricorder->data = (TricorderData) {0};

  if (tricorder->update_handler != NULL) {
    tricorder->update_handler(tricorder->data);
  }
}

void tricorder_set_update_handler(Tricorder *tricorder, TricorderUpdateHandler handler) {
  if (!tricorder) return;

  tricorder->update_handler = handler;
}

static void prv_update_data(Tricorder *tricorder) {
  if (!tricorder) return;

  tricorder->data->packet_id++;
  time_ms(&tricorder->data->timestamp, &tricorder->data->timestamp_ms);
  tricorder->data->connection_status = bluetooth_connection_service_peek();
  tricorder->data->charge_percent = battery_state_service_peek().charge_percent;
  accel_service_peek(&tricorder->data->accel_data);
  tricorder->data->crc32 = 0;
  tricorder->data->crc32 = crc32(tricorder->data, sizeof(TricorderData));

  if (tricorder->update_handler != NULL) {
    tricorder->update_handler(tricorder->data);
  }
}

static void prv_log_data(Tricorder *tricorder) {
  if (!tricorder) return;

  printf("==================================================");
  printf("packet_id:\t\t%d", (int) tricorder->data->packet_id);
  printf("timestamp:\t\t%d.%d", (int) tricorder->data->timestamp,
                                (int) tricorder->data->timestamp_ms);
  printf("connection_status:\t%d", (int) tricorder->data->connection_status);
  printf("charge_percent:\t%d", (int) tricorder->data->charge_percent);
  printf("accel:\t\t%05d\t%05d\t%05d\t%d", (int) tricorder->data->accel_data.x,
                                           (int) tricorder->data->accel_data.y,
                                           (int) tricorder->data->accel_data.z,
                                           (int) tricorder->data->accel_data.did_vibrate);
  printf("crc32:\t\t%d", (int) tricorder->data->crc32);
}

static void prv_add_data(Tricorder *tricorder) {
  if (!tricorder) return;

  DataLoggingResult res = data_logging_log(tricorder->logging_session, tricorder->data, 1);
  if (res != DATA_LOGGING_SUCCESS) {
    APP_LOG(APP_LOG_LEVEL_ERROR, "failed to add data to the logging session: %d", (int) res);
  }
}

static void prv_delay_timer_callback(void *data) {
  if (!data) return;

  Tricorder *tricorder = data;

  prv_update_data(tricorder);
  prv_log_data(tricorder);
  prv_add_data(tricorder);

  tricorder->delay_timer = app_timer_register(tricorder->delay_interval,
                                              prv_delay_timer_callback, data);
}

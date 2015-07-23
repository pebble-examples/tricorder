#include <pebble_worker.h>
#include "tricorder.h"
#include "common.h"

static Tricorder *s_tricorder = NULL;

static void tricorder_update_handler(TricorderData *tricorder_data) {
  TricorderData *data = tricorder_data;
  AppWorkerMessage msg = {
    .data0 = data->packet_id,
  };
  app_worker_send_message(WORKER_KEY_TRICORDER_UPDATE, &msg);
}

static void worker_message_handler(uint16_t type, AppWorkerMessage *data) {
  AppWorkerMessage msg;
  switch (type) {
    case WORKER_KEY_TRICORDER_START:
      tricorder_start_logging(s_tricorder);
      persist_write_bool(PERSIST_KEY_IS_LOGGING, true);
      break;

    case WORKER_KEY_TRICORDER_STOP:
      tricorder_stop_logging(s_tricorder);
      persist_write_bool(PERSIST_KEY_IS_LOGGING, false);
      break;

    case WORKER_KEY_TRICORDER_TOGGLE:
      if (tricorder_is_logging(s_tricorder)) {
        app_worker_send_message(WORKER_KEY_TRICORDER_STOP, &msg);
      } else {
        app_worker_send_message(WORKER_KEY_TRICORDER_START, &msg);
      }
      break;

    case WORKER_KEY_TRICORDER_RESET:
      tricorder_reset_data(s_tricorder);
      break;
  }
}

static void init() {
  app_worker_message_subscribe(worker_message_handler);

  s_tricorder = tricorder_create();
  tricorder_set_update_handler(s_tricorder, tricorder_update_handler);
}

static void deinit() {
  tricorder_destroy(s_tricorder);
  app_worker_message_unsubscribe();
  persist_write_bool(PERSIST_KEY_IS_LOGGING, false);
}

int main(void) {
  init();
  worker_event_loop();
  deinit();
}

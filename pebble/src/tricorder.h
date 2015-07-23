#pragma once

// TricorderData is the struct we send to mobile apps
// Any changes to this struct would require changes to mobile apps as well
typedef struct __attribute__((__packed__)) {
  uint32_t packet_id;      // 4 bytes
  time_t timestamp;        // 4 bytes
  uint16_t timestamp_ms;   // 2 bytes
  bool connection_status;  // 1 byte
  uint8_t charge_percent;  // 1 byte
  AccelData accel_data;    // 15 bytes
  int32_t crc32;           // 4 bytes
} TricorderData;           // 31 bytes

typedef struct Tricorder Tricorder;

typedef void (*TricorderUpdateHandler)(TricorderData *data);

Tricorder* tricorder_create(void);
void tricorder_destroy(Tricorder *tricorder);

void tricorder_start_logging(Tricorder *tricorder);
void tricorder_stop_logging(Tricorder *tricorder);
bool tricorder_is_logging(Tricorder *tricorder);

void tricorder_reset_data(Tricorder *tricorder);

void tricorder_set_update_handler(Tricorder *tricorder, TricorderUpdateHandler handler);

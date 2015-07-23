#include <pebble.h>
#include "crc32.h"

int32_t crc32(const void *buf, size_t len) {
  int crc  = 0xFFFFFFFF;
  int poly = 0xEDB88320;

  uint32_t i = 0;
  while (i < len) {
    int temp = (crc ^ ((char*)buf)[i]) & 0xff;

    for (int j = 0; j < 8; j++) {
      if ((temp & 1) == 1) temp = (temp >> 1) ^ poly;
      else                 temp = (temp >> 1);
    }
    crc = (crc >> 8) ^ temp;
    i++;
  }

  return crc ^ 0xffffffff;
}

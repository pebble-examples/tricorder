#include <pebble.h>
#include "common.h"

static Window *s_window = NULL;
static Layer *s_main_layer = NULL;
static uint32_t s_packet_id;

static void select_click_handler(ClickRecognizerRef recognizer, void *context) {
  AppWorkerMessage msg;
  app_worker_send_message(WORKER_KEY_TRICORDER_TOGGLE, &msg);
}

static void select_long_click_handler(ClickRecognizerRef recognizer, void *context) {
  AppWorkerMessage msg;
  app_worker_send_message(WORKER_KEY_TRICORDER_RESET, &msg);
}

static void click_config_provider(void *context) {
  window_single_click_subscribe(BUTTON_ID_SELECT, select_click_handler);
  window_long_click_subscribe(BUTTON_ID_SELECT, 1000, NULL, select_long_click_handler);
}

static void main_layer_update_proc(Layer *layer, GContext *ctx) {
  GRect bounds = layer_get_bounds(layer);

  bool small_font = (s_packet_id > 9999);

  graphics_context_set_fill_color(ctx, GColorWhite);
  graphics_fill_circle(ctx, GPoint(bounds.size.w / 2, bounds.size.h / 2),
                            (small_font ? 66 : 60));

  GRect text_bounds;
  text_bounds.origin.x = bounds.origin.x + 12;
  text_bounds.origin.y = (bounds.size.h / 2) - (small_font ? 20 : 30);
  text_bounds.size.w = bounds.size.w - (text_bounds.origin.x * 2);
  text_bounds.size.h = 64;

  static char s_packet_id_text[8];
  snprintf(s_packet_id_text, 8, "%d", (int) s_packet_id);

  GFont id_font = fonts_get_system_font(small_font ? FONT_KEY_LECO_26_BOLD_NUMBERS_AM_PM
                                                   : FONT_KEY_LECO_38_BOLD_NUMBERS);

  graphics_context_set_text_color(ctx, GColorBlack);
  graphics_draw_text(ctx, s_packet_id_text, id_font, text_bounds,
                     GTextOverflowModeFill, GTextAlignmentCenter, NULL);

  text_bounds.origin.y += (small_font ? 36 : 48);

  graphics_draw_text(ctx, "packets", fonts_get_system_font(FONT_KEY_GOTHIC_18_BOLD),
                     text_bounds, GTextOverflowModeFill, GTextAlignmentCenter, NULL);
}

static void window_load(Window *window) {
  Layer *window_layer = window_get_root_layer(window);
  GRect bounds = layer_get_bounds(window_layer);

  s_main_layer = layer_create(bounds);
  layer_set_update_proc(s_main_layer, main_layer_update_proc);
  layer_add_child(window_layer, s_main_layer);
}

static void window_unload(Window *window) {
  layer_destroy(s_main_layer);
}

static void worker_message_handler(uint16_t type, AppWorkerMessage *data) {
  switch (type) {
    case WORKER_KEY_TRICORDER_UPDATE:
      s_packet_id = data->data0;
      break;

    case WORKER_KEY_TRICORDER_START:
      window_set_background_color(s_window, GColorGreen);
      break;

    case WORKER_KEY_TRICORDER_STOP:
      window_set_background_color(s_window, GColorChromeYellow);
      break;
  }
  layer_mark_dirty(s_main_layer);
}

static void init(void) {
  app_worker_launch();
  app_worker_message_subscribe(worker_message_handler);

  s_window = window_create();
  window_set_background_color(s_window, GColorChromeYellow);
  window_set_click_config_provider(s_window, click_config_provider);
  window_set_window_handlers(s_window, (WindowHandlers) {
    .load = window_load,
    .unload = window_unload,
  });
  window_stack_push(s_window, true);

  if (persist_read_bool(PERSIST_KEY_IS_LOGGING)) {
    window_set_background_color(s_window, GColorGreen);
  }

  if (!app_worker_is_running()) {
    window_set_background_color(s_window, GColorRed);
  }
}

static void deinit(void) {
#ifdef PBL_SDK_2
  bluetooth_connection_service_unsubscribe();
#elif PBL_SDK_3
  connection_service_unsubscribe();
#endif
  window_destroy(s_window);
}

int main(void) {
  init();
  app_event_loop();
  deinit();
}

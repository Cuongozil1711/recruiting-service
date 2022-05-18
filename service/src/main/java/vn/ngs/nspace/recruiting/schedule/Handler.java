package vn.ngs.nspace.recruiting.schedule;

import java.util.Map;

/**
 * interface xử lý sự kiện khi nhận từ service schedule
 */

public interface Handler {
    public void process(Map<String, Object> data);
}

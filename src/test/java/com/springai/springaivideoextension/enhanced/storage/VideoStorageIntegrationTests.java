package com.springai.springaivideoextension.enhanced.storage;

import com.springai.springaivideoextension.enhanced.storage.impl.InMemoryVideoStorage;
import com.springai.springaivideoextension.enhanced.trimer.response.VideoScanResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VideoStorageIntegrationTests {

    private InMemoryVideoStorage videoStorage;

    @BeforeEach
    void setUp() {
        videoStorage = new InMemoryVideoStorage();
        videoStorage.setDefaultKey("in:memory:key:");
    }

    @Test
    void testStorageCreation() {
        assertThat(videoStorage).isNotNull();
    }

    @Test
    void testSaveVideoData() {
        VideoScanResponse videoData = new VideoScanResponse();
        boolean result = videoStorage.save("test-video-id", videoData);
        assertThat(result).isTrue();
    }

    @Test
    void testFindVideoById() {
        VideoScanResponse videoData = new VideoScanResponse();
        String key = "in:memory:key:test-video-id";
        videoStorage.save(key, videoData);
        
        Object foundData = videoStorage.findVideoById("test-video-id");
        assertThat(foundData).isNotNull();
    }

    @Test
    void testGetAllKeys() {
        VideoScanResponse videoData1 = new VideoScanResponse();
        VideoScanResponse videoData2 = new VideoScanResponse();
        videoStorage.save("video-1", videoData1);
        videoStorage.save("video-2", videoData2);
        
        Collection<String> keys = videoStorage.keys();
        assertThat(keys).isNotEmpty();
    }

    @Test
    void testUpdateVideoStatus() {
        // 保存一个视频任务
        VideoScanResponse videoData = new VideoScanResponse();
        String key = "in:memory:key:test-request-id";
        videoStorage.save(key, videoData);
        
        // 更新状态
        VideoScanResponse scanResponse = new VideoScanResponse();
        videoStorage.changeStatus(
            "test-request-id", 
            VideoStorageStatus.SUCCESS, 
            scanResponse
        );
    }

    @Test
    void testRemoveDefaultPrefix() {
        VideoScanResponse videoData1 = new VideoScanResponse();
        VideoScanResponse videoData2 = new VideoScanResponse();
        videoStorage.save("test-key-1", videoData1);
        videoStorage.save("test-key-2", videoData2);
        
        Collection<String> keys = videoStorage.keys();
        Collection<String> cleanedKeys = videoStorage.removeDefaultPrefix(keys);
        
        assertThat(cleanedKeys).isNotEmpty();
    }

    @Test
    void testDeleteVideo() {
        // 先保存
        VideoScanResponse videoData = new VideoScanResponse();
        String key = "in:memory:key:to-delete-id";
        videoStorage.save(key, videoData);
        assertThat(videoStorage.findVideoById("to-delete-id")).isNotNull();
        
        // 删除
        videoStorage.delete(key);
    }
}
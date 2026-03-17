package com.multiagent.toolagent.ragagent.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Map;

@Configuration
public class RAGConfig {

    @Bean
    public EmbeddingStoreIngestor embeddingStoreIngestor(
            EmbeddingStore<TextSegment> embeddingStore,
            EmbeddingModel embeddingModel) {

        return EmbeddingStoreIngestor.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .documentSplitter(DocumentSplitters.recursive(500, 100))
                // 关键修改：使用 documentTransformer 添加元数据
                .documentTransformer(document -> {
                    // 获取文件名（假设文档加载时已有 file_name 元数据）
                    String fileName = document.metadata().getString("file_name");
                    // 构建新的元数据对象
                    Metadata metadata = Metadata.from(
                            Map.of(
                                    "source", fileName != null ? fileName : "unknown",
                                    "category", "knowledge",
                                    "loaded_at", LocalDateTime.now().toString()
                            )
                    );
                    // 返回携带新元数据的文档
                    return Document.from(document.text(), metadata);
                })
                .build();
    }
}

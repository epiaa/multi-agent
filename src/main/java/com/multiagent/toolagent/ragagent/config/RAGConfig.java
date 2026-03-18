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
import java.util.HashMap;
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
                    Map<String, Object> metaMap = new HashMap<>(document.metadata().toMap());

                    String fileName = (String) metaMap.get("file_name");

                    // 增量补充，而不是覆盖
                    metaMap.put("source", fileName != null ? fileName : "unknown");
                    metaMap.put("category", "knowledge");
                    metaMap.put("loaded_at", LocalDateTime.now().toString());

                    return Document.from(document.text(), Metadata.from(metaMap));
                })
                .build();
    }
}

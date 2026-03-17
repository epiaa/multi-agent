package com.multiagent.toolagent.ragagent.config;

import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class PgVectorStoreConfig {

    @Bean
    public PgVectorEmbeddingStore embeddingStore() {
        return PgVectorEmbeddingStore.builder()
                .host("192.168.110.129")
                .port(5432)
                .database("ai_vector")
                .user("postgres")
                .password("root")
                .table("rag_documents")
                .dimension(384)
                .build();
    }

}

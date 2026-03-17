package com.multiagent.toolagent.ragagent.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RagRetrieverConfig {

    @Bean
    public ContentRetriever contentRetriever(
            EmbeddingModel embeddingModel,
            PgVectorEmbeddingStore embeddingStore) {

        return EmbeddingStoreContentRetriever.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .maxResults(3)
                .minScore(0.7)
                .build();
    }

}

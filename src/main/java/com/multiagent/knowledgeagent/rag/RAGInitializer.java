package com.multiagent.knowledgeagent.rag;

import com.multiagent.knowledgeagent.business.service.RagDocumentService;
import com.multiagent.knowledgeagent.entity.RagDocument;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RAGInitializer implements ApplicationRunner {

    private final RagDocumentService ragDocumentService;

    private final EmbeddingStoreIngestor ingestor;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Path docsDir = Path.of("src/main/resources/data");

        // 1. 获取所有文件
        List<File> files = Files.walk(docsDir)
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .toList();

        // 2. 过滤已处理的
        List<File> newFiles = files.stream()
                .filter(file -> {
                    String hash = null;
                    try {
                        hash = calculateFileHash(file);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return ragDocumentService.lambdaQuery()
                            .eq(RagDocument::getFileHash, hash)
                            .count() == 0;
                })
                .toList();

        if (newFiles.isEmpty()) {
            System.out.println("没有新文档需要加载");
            return;
        }

        // 3. 批量处理新文件
        List<Document> documents = new ArrayList<>();
        for (File file : newFiles) {
            try {
                String text = Files.readString(file.toPath());
                Document doc = Document.from(text, Metadata.from(
                        Map.of(
                                "file_name", file.getName(),
                                "file_hash", calculateFileHash(file),
                                "file_size", String.valueOf(file.length())
                        )
                ));
                documents.add(doc);
            } catch (Exception e) {
                System.err.println("读取失败: " + file.getName());
            }
        }

        // 4. 批量向量化存储
        ingestor.ingest(documents);

        // 5. 记录到数据库
        for (File file : newFiles) {
            RagDocument doc = RagDocument.builder()
                    .documentId(UUID.randomUUID().toString())
                    .fileName(file.getName())
                    .fileHash(calculateFileHash(file))
                    .build();
            ragDocumentService.save(doc);
        }

        System.out.println("增量加载完成，新文档: " + newFiles.size());
    }

    private String calculateFileHash(File file) throws Exception {

        MessageDigest digest = MessageDigest.getInstance("MD5");

        byte[] bytes = Files.readAllBytes(file.toPath());

        byte[] hash = digest.digest(bytes);

        StringBuilder hex = new StringBuilder();

        for (byte b : hash) {
            hex.append(String.format("%02x", b));
        }

        return hex.toString();
    }
}
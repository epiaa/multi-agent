package com.multiagent.knowledgeagent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@TableName("rag_document")
public class RagDocument {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String documentId;
    private String fileName;
    private String fileHash;
    private LocalDateTime createdAt;

}

package com.qianqiu.clouddisk.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @description Minio Bucket访问策略配置
 */
@Data
@EqualsAndHashCode
@Builder
public class BucketPolicyConfigDTO {

    private String Version;
    private List<Statement> Statement;

    @Data
    @EqualsAndHashCode
    @Builder
    public static class Statement {
        private String Effect;
        private String Principal;
        private String Action;
        private String Resource;

    }
}
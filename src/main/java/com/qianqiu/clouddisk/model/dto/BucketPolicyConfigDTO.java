package com.qianqiu.clouddisk.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

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
        private String Principal; // 修改Principal类型为Principal类
        private String Action;
        private String Resource;
        private String NotResource;

    }

}
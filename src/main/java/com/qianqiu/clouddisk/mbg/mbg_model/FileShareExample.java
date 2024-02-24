package com.qianqiu.clouddisk.mbg.mbg_model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileShareExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public FileShareExample() {
        oredCriteria = new ArrayList<>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andShareIdIsNull() {
            addCriterion("share_id is null");
            return (Criteria) this;
        }

        public Criteria andShareIdIsNotNull() {
            addCriterion("share_id is not null");
            return (Criteria) this;
        }

        public Criteria andShareIdEqualTo(String value) {
            addCriterion("share_id =", value, "shareId");
            return (Criteria) this;
        }

        public Criteria andShareIdNotEqualTo(String value) {
            addCriterion("share_id <>", value, "shareId");
            return (Criteria) this;
        }

        public Criteria andShareIdGreaterThan(String value) {
            addCriterion("share_id >", value, "shareId");
            return (Criteria) this;
        }

        public Criteria andShareIdGreaterThanOrEqualTo(String value) {
            addCriterion("share_id >=", value, "shareId");
            return (Criteria) this;
        }

        public Criteria andShareIdLessThan(String value) {
            addCriterion("share_id <", value, "shareId");
            return (Criteria) this;
        }

        public Criteria andShareIdLessThanOrEqualTo(String value) {
            addCriterion("share_id <=", value, "shareId");
            return (Criteria) this;
        }

        public Criteria andShareIdLike(String value) {
            addCriterion("share_id like", value, "shareId");
            return (Criteria) this;
        }

        public Criteria andShareIdNotLike(String value) {
            addCriterion("share_id not like", value, "shareId");
            return (Criteria) this;
        }

        public Criteria andShareIdIn(List<String> values) {
            addCriterion("share_id in", values, "shareId");
            return (Criteria) this;
        }

        public Criteria andShareIdNotIn(List<String> values) {
            addCriterion("share_id not in", values, "shareId");
            return (Criteria) this;
        }

        public Criteria andShareIdBetween(String value1, String value2) {
            addCriterion("share_id between", value1, value2, "shareId");
            return (Criteria) this;
        }

        public Criteria andShareIdNotBetween(String value1, String value2) {
            addCriterion("share_id not between", value1, value2, "shareId");
            return (Criteria) this;
        }

        public Criteria andFileIdIsNull() {
            addCriterion("file_id is null");
            return (Criteria) this;
        }

        public Criteria andFileIdIsNotNull() {
            addCriterion("file_id is not null");
            return (Criteria) this;
        }

        public Criteria andFileIdEqualTo(String value) {
            addCriterion("file_id =", value, "fileId");
            return (Criteria) this;
        }

        public Criteria andFileIdNotEqualTo(String value) {
            addCriterion("file_id <>", value, "fileId");
            return (Criteria) this;
        }

        public Criteria andFileIdGreaterThan(String value) {
            addCriterion("file_id >", value, "fileId");
            return (Criteria) this;
        }

        public Criteria andFileIdGreaterThanOrEqualTo(String value) {
            addCriterion("file_id >=", value, "fileId");
            return (Criteria) this;
        }

        public Criteria andFileIdLessThan(String value) {
            addCriterion("file_id <", value, "fileId");
            return (Criteria) this;
        }

        public Criteria andFileIdLessThanOrEqualTo(String value) {
            addCriterion("file_id <=", value, "fileId");
            return (Criteria) this;
        }

        public Criteria andFileIdLike(String value) {
            addCriterion("file_id like", value, "fileId");
            return (Criteria) this;
        }

        public Criteria andFileIdNotLike(String value) {
            addCriterion("file_id not like", value, "fileId");
            return (Criteria) this;
        }

        public Criteria andFileIdIn(List<String> values) {
            addCriterion("file_id in", values, "fileId");
            return (Criteria) this;
        }

        public Criteria andFileIdNotIn(List<String> values) {
            addCriterion("file_id not in", values, "fileId");
            return (Criteria) this;
        }

        public Criteria andFileIdBetween(String value1, String value2) {
            addCriterion("file_id between", value1, value2, "fileId");
            return (Criteria) this;
        }

        public Criteria andFileIdNotBetween(String value1, String value2) {
            addCriterion("file_id not between", value1, value2, "fileId");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNull() {
            addCriterion("user_id is null");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNotNull() {
            addCriterion("user_id is not null");
            return (Criteria) this;
        }

        public Criteria andUserIdEqualTo(String value) {
            addCriterion("user_id =", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotEqualTo(String value) {
            addCriterion("user_id <>", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThan(String value) {
            addCriterion("user_id >", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThanOrEqualTo(String value) {
            addCriterion("user_id >=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThan(String value) {
            addCriterion("user_id <", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThanOrEqualTo(String value) {
            addCriterion("user_id <=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLike(String value) {
            addCriterion("user_id like", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotLike(String value) {
            addCriterion("user_id not like", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdIn(List<String> values) {
            addCriterion("user_id in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotIn(List<String> values) {
            addCriterion("user_id not in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdBetween(String value1, String value2) {
            addCriterion("user_id between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotBetween(String value1, String value2) {
            addCriterion("user_id not between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andValidTypeIsNull() {
            addCriterion("valid_type is null");
            return (Criteria) this;
        }

        public Criteria andValidTypeIsNotNull() {
            addCriterion("valid_type is not null");
            return (Criteria) this;
        }

        public Criteria andValidTypeEqualTo(Integer value) {
            addCriterion("valid_type =", value, "validType");
            return (Criteria) this;
        }

        public Criteria andValidTypeNotEqualTo(Integer value) {
            addCriterion("valid_type <>", value, "validType");
            return (Criteria) this;
        }

        public Criteria andValidTypeGreaterThan(Integer value) {
            addCriterion("valid_type >", value, "validType");
            return (Criteria) this;
        }

        public Criteria andValidTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("valid_type >=", value, "validType");
            return (Criteria) this;
        }

        public Criteria andValidTypeLessThan(Integer value) {
            addCriterion("valid_type <", value, "validType");
            return (Criteria) this;
        }

        public Criteria andValidTypeLessThanOrEqualTo(Integer value) {
            addCriterion("valid_type <=", value, "validType");
            return (Criteria) this;
        }

        public Criteria andValidTypeIn(List<Integer> values) {
            addCriterion("valid_type in", values, "validType");
            return (Criteria) this;
        }

        public Criteria andValidTypeNotIn(List<Integer> values) {
            addCriterion("valid_type not in", values, "validType");
            return (Criteria) this;
        }

        public Criteria andValidTypeBetween(Integer value1, Integer value2) {
            addCriterion("valid_type between", value1, value2, "validType");
            return (Criteria) this;
        }

        public Criteria andValidTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("valid_type not between", value1, value2, "validType");
            return (Criteria) this;
        }

        public Criteria andExpireTimeIsNull() {
            addCriterion("expire_time is null");
            return (Criteria) this;
        }

        public Criteria andExpireTimeIsNotNull() {
            addCriterion("expire_time is not null");
            return (Criteria) this;
        }

        public Criteria andExpireTimeEqualTo(Date value) {
            addCriterion("expire_time =", value, "expireTime");
            return (Criteria) this;
        }

        public Criteria andExpireTimeNotEqualTo(Date value) {
            addCriterion("expire_time <>", value, "expireTime");
            return (Criteria) this;
        }

        public Criteria andExpireTimeGreaterThan(Date value) {
            addCriterion("expire_time >", value, "expireTime");
            return (Criteria) this;
        }

        public Criteria andExpireTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("expire_time >=", value, "expireTime");
            return (Criteria) this;
        }

        public Criteria andExpireTimeLessThan(Date value) {
            addCriterion("expire_time <", value, "expireTime");
            return (Criteria) this;
        }

        public Criteria andExpireTimeLessThanOrEqualTo(Date value) {
            addCriterion("expire_time <=", value, "expireTime");
            return (Criteria) this;
        }

        public Criteria andExpireTimeIn(List<Date> values) {
            addCriterion("expire_time in", values, "expireTime");
            return (Criteria) this;
        }

        public Criteria andExpireTimeNotIn(List<Date> values) {
            addCriterion("expire_time not in", values, "expireTime");
            return (Criteria) this;
        }

        public Criteria andExpireTimeBetween(Date value1, Date value2) {
            addCriterion("expire_time between", value1, value2, "expireTime");
            return (Criteria) this;
        }

        public Criteria andExpireTimeNotBetween(Date value1, Date value2) {
            addCriterion("expire_time not between", value1, value2, "expireTime");
            return (Criteria) this;
        }

        public Criteria andShareTimeIsNull() {
            addCriterion("share_time is null");
            return (Criteria) this;
        }

        public Criteria andShareTimeIsNotNull() {
            addCriterion("share_time is not null");
            return (Criteria) this;
        }

        public Criteria andShareTimeEqualTo(Date value) {
            addCriterion("share_time =", value, "shareTime");
            return (Criteria) this;
        }

        public Criteria andShareTimeNotEqualTo(Date value) {
            addCriterion("share_time <>", value, "shareTime");
            return (Criteria) this;
        }

        public Criteria andShareTimeGreaterThan(Date value) {
            addCriterion("share_time >", value, "shareTime");
            return (Criteria) this;
        }

        public Criteria andShareTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("share_time >=", value, "shareTime");
            return (Criteria) this;
        }

        public Criteria andShareTimeLessThan(Date value) {
            addCriterion("share_time <", value, "shareTime");
            return (Criteria) this;
        }

        public Criteria andShareTimeLessThanOrEqualTo(Date value) {
            addCriterion("share_time <=", value, "shareTime");
            return (Criteria) this;
        }

        public Criteria andShareTimeIn(List<Date> values) {
            addCriterion("share_time in", values, "shareTime");
            return (Criteria) this;
        }

        public Criteria andShareTimeNotIn(List<Date> values) {
            addCriterion("share_time not in", values, "shareTime");
            return (Criteria) this;
        }

        public Criteria andShareTimeBetween(Date value1, Date value2) {
            addCriterion("share_time between", value1, value2, "shareTime");
            return (Criteria) this;
        }

        public Criteria andShareTimeNotBetween(Date value1, Date value2) {
            addCriterion("share_time not between", value1, value2, "shareTime");
            return (Criteria) this;
        }

        public Criteria andCodeIsNull() {
            addCriterion("code is null");
            return (Criteria) this;
        }

        public Criteria andCodeIsNotNull() {
            addCriterion("code is not null");
            return (Criteria) this;
        }

        public Criteria andCodeEqualTo(String value) {
            addCriterion("code =", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeNotEqualTo(String value) {
            addCriterion("code <>", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeGreaterThan(String value) {
            addCriterion("code >", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeGreaterThanOrEqualTo(String value) {
            addCriterion("code >=", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeLessThan(String value) {
            addCriterion("code <", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeLessThanOrEqualTo(String value) {
            addCriterion("code <=", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeLike(String value) {
            addCriterion("code like", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeNotLike(String value) {
            addCriterion("code not like", value, "code");
            return (Criteria) this;
        }

        public Criteria andCodeIn(List<String> values) {
            addCriterion("code in", values, "code");
            return (Criteria) this;
        }

        public Criteria andCodeNotIn(List<String> values) {
            addCriterion("code not in", values, "code");
            return (Criteria) this;
        }

        public Criteria andCodeBetween(String value1, String value2) {
            addCriterion("code between", value1, value2, "code");
            return (Criteria) this;
        }

        public Criteria andCodeNotBetween(String value1, String value2) {
            addCriterion("code not between", value1, value2, "code");
            return (Criteria) this;
        }

        public Criteria andShowCountIsNull() {
            addCriterion("show_count is null");
            return (Criteria) this;
        }

        public Criteria andShowCountIsNotNull() {
            addCriterion("show_count is not null");
            return (Criteria) this;
        }

        public Criteria andShowCountEqualTo(Integer value) {
            addCriterion("show_count =", value, "showCount");
            return (Criteria) this;
        }

        public Criteria andShowCountNotEqualTo(Integer value) {
            addCriterion("show_count <>", value, "showCount");
            return (Criteria) this;
        }

        public Criteria andShowCountGreaterThan(Integer value) {
            addCriterion("show_count >", value, "showCount");
            return (Criteria) this;
        }

        public Criteria andShowCountGreaterThanOrEqualTo(Integer value) {
            addCriterion("show_count >=", value, "showCount");
            return (Criteria) this;
        }

        public Criteria andShowCountLessThan(Integer value) {
            addCriterion("show_count <", value, "showCount");
            return (Criteria) this;
        }

        public Criteria andShowCountLessThanOrEqualTo(Integer value) {
            addCriterion("show_count <=", value, "showCount");
            return (Criteria) this;
        }

        public Criteria andShowCountIn(List<Integer> values) {
            addCriterion("show_count in", values, "showCount");
            return (Criteria) this;
        }

        public Criteria andShowCountNotIn(List<Integer> values) {
            addCriterion("show_count not in", values, "showCount");
            return (Criteria) this;
        }

        public Criteria andShowCountBetween(Integer value1, Integer value2) {
            addCriterion("show_count between", value1, value2, "showCount");
            return (Criteria) this;
        }

        public Criteria andShowCountNotBetween(Integer value1, Integer value2) {
            addCriterion("show_count not between", value1, value2, "showCount");
            return (Criteria) this;
        }

        public Criteria andSharePathIsNull() {
            addCriterion("share_path is null");
            return (Criteria) this;
        }

        public Criteria andSharePathIsNotNull() {
            addCriterion("share_path is not null");
            return (Criteria) this;
        }

        public Criteria andSharePathEqualTo(String value) {
            addCriterion("share_path =", value, "sharePath");
            return (Criteria) this;
        }

        public Criteria andSharePathNotEqualTo(String value) {
            addCriterion("share_path <>", value, "sharePath");
            return (Criteria) this;
        }

        public Criteria andSharePathGreaterThan(String value) {
            addCriterion("share_path >", value, "sharePath");
            return (Criteria) this;
        }

        public Criteria andSharePathGreaterThanOrEqualTo(String value) {
            addCriterion("share_path >=", value, "sharePath");
            return (Criteria) this;
        }

        public Criteria andSharePathLessThan(String value) {
            addCriterion("share_path <", value, "sharePath");
            return (Criteria) this;
        }

        public Criteria andSharePathLessThanOrEqualTo(String value) {
            addCriterion("share_path <=", value, "sharePath");
            return (Criteria) this;
        }

        public Criteria andSharePathLike(String value) {
            addCriterion("share_path like", value, "sharePath");
            return (Criteria) this;
        }

        public Criteria andSharePathNotLike(String value) {
            addCriterion("share_path not like", value, "sharePath");
            return (Criteria) this;
        }

        public Criteria andSharePathIn(List<String> values) {
            addCriterion("share_path in", values, "sharePath");
            return (Criteria) this;
        }

        public Criteria andSharePathNotIn(List<String> values) {
            addCriterion("share_path not in", values, "sharePath");
            return (Criteria) this;
        }

        public Criteria andSharePathBetween(String value1, String value2) {
            addCriterion("share_path between", value1, value2, "sharePath");
            return (Criteria) this;
        }

        public Criteria andSharePathNotBetween(String value1, String value2) {
            addCriterion("share_path not between", value1, value2, "sharePath");
            return (Criteria) this;
        }

        public Criteria andWebShareUrlIsNull() {
            addCriterion("web_share_url is null");
            return (Criteria) this;
        }

        public Criteria andWebShareUrlIsNotNull() {
            addCriterion("web_share_url is not null");
            return (Criteria) this;
        }

        public Criteria andWebShareUrlEqualTo(String value) {
            addCriterion("web_share_url =", value, "webShareUrl");
            return (Criteria) this;
        }

        public Criteria andWebShareUrlNotEqualTo(String value) {
            addCriterion("web_share_url <>", value, "webShareUrl");
            return (Criteria) this;
        }

        public Criteria andWebShareUrlGreaterThan(String value) {
            addCriterion("web_share_url >", value, "webShareUrl");
            return (Criteria) this;
        }

        public Criteria andWebShareUrlGreaterThanOrEqualTo(String value) {
            addCriterion("web_share_url >=", value, "webShareUrl");
            return (Criteria) this;
        }

        public Criteria andWebShareUrlLessThan(String value) {
            addCriterion("web_share_url <", value, "webShareUrl");
            return (Criteria) this;
        }

        public Criteria andWebShareUrlLessThanOrEqualTo(String value) {
            addCriterion("web_share_url <=", value, "webShareUrl");
            return (Criteria) this;
        }

        public Criteria andWebShareUrlLike(String value) {
            addCriterion("web_share_url like", value, "webShareUrl");
            return (Criteria) this;
        }

        public Criteria andWebShareUrlNotLike(String value) {
            addCriterion("web_share_url not like", value, "webShareUrl");
            return (Criteria) this;
        }

        public Criteria andWebShareUrlIn(List<String> values) {
            addCriterion("web_share_url in", values, "webShareUrl");
            return (Criteria) this;
        }

        public Criteria andWebShareUrlNotIn(List<String> values) {
            addCriterion("web_share_url not in", values, "webShareUrl");
            return (Criteria) this;
        }

        public Criteria andWebShareUrlBetween(String value1, String value2) {
            addCriterion("web_share_url between", value1, value2, "webShareUrl");
            return (Criteria) this;
        }

        public Criteria andWebShareUrlNotBetween(String value1, String value2) {
            addCriterion("web_share_url not between", value1, value2, "webShareUrl");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {
        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}
package org.example.passjava.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName sms_event_outbox
 */
@TableName(value ="sms_event_outbox")
@Data
public class SmsEventOutbox {
    /**
     * 
     */
    @TableId
    private String eventId;

    /**
     * 
     */
    private Long userId;

    /**
     * 
     */
    private Long questionId;

    /**
     * 
     */
    private Date occurredAt;

    /**
     0	PENDING：等待发送
     1	SENDING：某个发布器已经领取事件
     2	SENT：Broker 已确认投递并通过路由检查
     3	FAILED：达到最大重试次数，需要人工处理
     */
    private Integer status;

    /**
     * 
     */
    private Integer retryCount;

    /**
     * 
     */
    private Date nextRetryAt;

    /**
     * 
     */
    private Date lockedAt;

    /**
     * 
     */
    private String lastError;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        SmsEventOutbox other = (SmsEventOutbox) that;
        return (this.getEventId() == null ? other.getEventId() == null : this.getEventId().equals(other.getEventId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getQuestionId() == null ? other.getQuestionId() == null : this.getQuestionId().equals(other.getQuestionId()))
            && (this.getOccurredAt() == null ? other.getOccurredAt() == null : this.getOccurredAt().equals(other.getOccurredAt()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getRetryCount() == null ? other.getRetryCount() == null : this.getRetryCount().equals(other.getRetryCount()))
            && (this.getNextRetryAt() == null ? other.getNextRetryAt() == null : this.getNextRetryAt().equals(other.getNextRetryAt()))
            && (this.getLockedAt() == null ? other.getLockedAt() == null : this.getLockedAt().equals(other.getLockedAt()))
            && (this.getLastError() == null ? other.getLastError() == null : this.getLastError().equals(other.getLastError()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getEventId() == null) ? 0 : getEventId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getQuestionId() == null) ? 0 : getQuestionId().hashCode());
        result = prime * result + ((getOccurredAt() == null) ? 0 : getOccurredAt().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getRetryCount() == null) ? 0 : getRetryCount().hashCode());
        result = prime * result + ((getNextRetryAt() == null) ? 0 : getNextRetryAt().hashCode());
        result = prime * result + ((getLockedAt() == null) ? 0 : getLockedAt().hashCode());
        result = prime * result + ((getLastError() == null) ? 0 : getLastError().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", eventId=").append(eventId);
        sb.append(", userId=").append(userId);
        sb.append(", questionId=").append(questionId);
        sb.append(", occurredAt=").append(occurredAt);
        sb.append(", status=").append(status);
        sb.append(", retryCount=").append(retryCount);
        sb.append(", nextRetryAt=").append(nextRetryAt);
        sb.append(", lockedAt=").append(lockedAt);
        sb.append(", lastError=").append(lastError);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}
package org.example.passjava.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName sms_processed_event
 */
@TableName(value ="sms_processed_event")
@Data
public class SmsProcessedEvent {
    /**
     * 
     */
    @TableId
    private String eventId;

    /**
     * 
     */
    private Date processedTime;

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
        SmsProcessedEvent other = (SmsProcessedEvent) that;
        return (this.getEventId() == null ? other.getEventId() == null : this.getEventId().equals(other.getEventId()))
            && (this.getProcessedTime() == null ? other.getProcessedTime() == null : this.getProcessedTime().equals(other.getProcessedTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getEventId() == null) ? 0 : getEventId().hashCode());
        result = prime * result + ((getProcessedTime() == null) ? 0 : getProcessedTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", eventId=").append(eventId);
        sb.append(", processedTime=").append(processedTime);
        sb.append("]");
        return sb.toString();
    }
}
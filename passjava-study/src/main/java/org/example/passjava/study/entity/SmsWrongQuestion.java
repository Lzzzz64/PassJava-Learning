package org.example.passjava.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName sms_wrong_question
 */
@TableName(value ="sms_wrong_question")
@Data
public class SmsWrongQuestion {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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
    private Integer wrongCount;

    /**
     * 
     */
    private Date lastWrongTime;

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
        SmsWrongQuestion other = (SmsWrongQuestion) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getQuestionId() == null ? other.getQuestionId() == null : this.getQuestionId().equals(other.getQuestionId()))
            && (this.getWrongCount() == null ? other.getWrongCount() == null : this.getWrongCount().equals(other.getWrongCount()))
            && (this.getLastWrongTime() == null ? other.getLastWrongTime() == null : this.getLastWrongTime().equals(other.getLastWrongTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getQuestionId() == null) ? 0 : getQuestionId().hashCode());
        result = prime * result + ((getWrongCount() == null) ? 0 : getWrongCount().hashCode());
        result = prime * result + ((getLastWrongTime() == null) ? 0 : getLastWrongTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", questionId=").append(questionId);
        sb.append(", wrongCount=").append(wrongCount);
        sb.append(", lastWrongTime=").append(lastWrongTime);
        sb.append("]");
        return sb.toString();
    }
}
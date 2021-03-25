package org.jee.boot.client.web.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class BaseEntity implements Serializable {
    @Id
    protected String id;
    protected Date createdTime;
    protected Date updatedTime;
}

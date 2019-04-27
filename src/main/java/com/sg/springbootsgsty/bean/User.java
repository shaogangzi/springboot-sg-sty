package com.sg.springbootsgsty.bean;

import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Accessors(chain = true)
public class User {
    private Long id;
    private String userCode;
    private String name;
    private int age;
    private String createTime;
    private String updateTime;
}

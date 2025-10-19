package com.ftn.sbnz.model;

import lombok.Data;
import java.util.List;

@Data
public class AccessibleResource {
    private String id;     // cloudId
    private String url;    // site URL
    private String name;
    private List<String> scopes;
}

package org.lamisplus.modules.base.module;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
public class UMDModule implements Comparable<UMDModule>{
    @NonNull
    private String name;
    @NonNull
    private String path;
    @Setter
    private String map = "{}";
    @NonNull
    private String content;

    public UMDModule(@NonNull String name, @NonNull String path, @NonNull String content){
        this.name = name;
        this.path = path;
        this.content = content;
    }

    @Override
    public int compareTo(UMDModule o) {
        return name.compareTo(o.name);
    }
}

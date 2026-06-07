package com.duoc.guias_despacho_cloud.modelo;

import java.net.URL;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Data
@Value
@Builder
public class Asset {

    private String name;
    private String key;
    private URL url;

}

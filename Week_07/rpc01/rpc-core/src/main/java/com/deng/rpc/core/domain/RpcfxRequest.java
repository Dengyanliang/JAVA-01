package com.deng.rpc.core.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement
public class RpcfxRequest {
  private String serviceClass;
  private String method;
  private Object[] params;
}

package org.pdown.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.nio.NioEventLoopGroup;
import javax.servlet.http.HttpServletRequest;
import org.pdown.core.entity.HttpRequestInfo;
import org.pdown.core.entity.HttpResponseInfo;
import org.pdown.core.util.HttpDownUtil;
import org.pdown.rest.base.exception.ParameterException;
import org.pdown.rest.entity.HttpResult;
import org.pdown.rest.form.HttpRequestForm;
import org.pdown.rest.util.RestUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("util")
public class UtilController {

  /*
    Resolve request
     */
  @PostMapping("resolve")
  public ResponseEntity<HttpResult> resolve(HttpServletRequest request) throws Exception {
    NioEventLoopGroup loopGroup = null;
    try {
      ObjectMapper mapper = new ObjectMapper();
      HttpRequestForm requestForm = mapper.readValue(request.getInputStream(), HttpRequestForm.class);
      if (StringUtils.isEmpty(requestForm.getUrl())) {
        throw new ParameterException("url can't be empty");
      }
      HttpRequestInfo httpRequestInfo = HttpDownUtil.buildGetRequest(requestForm.getUrl(), requestForm.getHeads(), requestForm.getBody());
      loopGroup = new NioEventLoopGroup(1);
      HttpResponseInfo httpResponseInfo = HttpDownUtil.getHttpResponseInfo(httpRequestInfo, null, null, loopGroup);
      return RestUtil.buildResponse(httpResponseInfo);
    } finally {
      if (loopGroup != null) {
        loopGroup.shutdownGracefully();
      }
    }
  }
}

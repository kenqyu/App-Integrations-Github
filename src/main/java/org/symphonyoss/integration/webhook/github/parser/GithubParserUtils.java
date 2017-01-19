/**
 * Copyright 2016-2017 Symphony Integrations - Symphony LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.symphonyoss.integration.webhook.github.parser;

import org.symphonyoss.integration.json.JsonUtils;

import com.fasterxml.jackson.databind.JsonNode;
import org.glassfish.jersey.client.ClientProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Utilities class to help validate messages from GitHub.
 *
 * Created by Milton Quilzini on 13/09/16.
 */
@Lazy
@Component
public class GithubParserUtils {

  private Client baseClientTargetBuilder;

  public GithubParserUtils() {
    baseClientTargetBuilder = ClientBuilder.newBuilder().build();
    baseClientTargetBuilder.property(ClientProperties.CONNECT_TIMEOUT, 15000);
    baseClientTargetBuilder.property(ClientProperties.READ_TIMEOUT, 15000);
  }

  /**
   * Hits an URL with http GET method, without any authentication.
   * Expects and returns a formatted json as an answer, null otherwise.
   *
   * @param url the URL to hit.
   * @return expects and returns a formatted JSON as an answer, null otherwise.
   * @throws IOException if something goes wrong while converting the answer into a JSON.
   */
  public JsonNode doGetJsonApi(String url) throws IOException {
    WebTarget githubWebTarget = baseClientTargetBuilder.target(url);

    Response response = null;
    try {
      response = githubWebTarget.request().accept(MediaType.APPLICATION_JSON_TYPE).get();
      if (response.getStatus() == HttpServletResponse.SC_OK) {
        return JsonUtils.readTree((InputStream) response.getEntity());
      } else {
        return null;
      }
    } finally {
      if (response != null) {
        response.close();
      }
    }
  }
}
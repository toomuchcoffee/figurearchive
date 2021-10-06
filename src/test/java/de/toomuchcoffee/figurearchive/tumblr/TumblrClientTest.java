package de.toomuchcoffee.figurearchive.tumblr;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.DOCKER;
import static java.nio.charset.Charset.defaultCharset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.util.StreamUtils.copyToString;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureEmbeddedDatabase(provider = DOCKER)
@ContextConfiguration(classes = {WireMockConfig.class})
public class TumblrClientTest {

    @Autowired
    private WireMockServer mockService;

    @Autowired
    private TumblrClient tumblrClient;

    @Before
    public void setUp() throws Exception {
        mockService.stubFor(WireMock.get(urlEqualTo("/v2/blog/yaswb/info?api_key=foo"))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                        .withBody(copyToString(
                                TumblrClient.class.getClassLoader().getResourceAsStream("info-response.json"),
                                defaultCharset()))));

        mockService.stubFor(WireMock.get(urlEqualTo("/v2/blog/yaswb/posts?npf=true&api_key=foo&offset=0&limit=10"))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                        .withBody(copyToString(
                                TumblrClient.class.getClassLoader().getResourceAsStream("posts-response.json"),
                                defaultCharset()))));
    }

    @Test
    public void shouldGetBlogInfo() {
        ResponseWrapper responseWrapper = tumblrClient.getInfo("foo");

        assertThat(responseWrapper).isNotNull();
        assertThat(responseWrapper.getResponse()).isNotNull();
        assertThat(responseWrapper.getResponse().getBlog()).isNotNull();
        assertThat(responseWrapper.getResponse().getBlog().getTotalPosts()).isEqualTo(2271);
    }

    @Test
    public void shouldGetPosts() {
        ResponseWrapper responseWrapper = tumblrClient.getPosts("foo", 0, 10);

        assertThat(responseWrapper).isNotNull();
        assertThat(responseWrapper.getResponse()).isNotNull();
        assertThat(responseWrapper.getResponse().getBlog()).isNotNull();
        List<BlocksPost> posts = responseWrapper.getResponse().getPosts();
        assertThat(posts).hasSize(10);
        assertThat(posts.get(0).getType()).isEqualTo("blocks");
        assertThat(posts.get(0).getTags()).hasSize(2);
        List<Content> content = posts.get(0).getContent();
        assertThat(content).isNotEmpty();
        assertThat(content.get(0).getType()).isEqualTo("image");
        assertThat(content.get(0).getMedia()).isNotEmpty();
        assertThat(content.get(0).getMedia().get(0).getWidth()).isEqualTo(2048);
    }
}
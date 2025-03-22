package art.aelaort.habr;

import art.aelaort.habr.rss.RssFeed;
import art.aelaort.habr.rss.RssMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class HabrClient {
	private final RssMapper rssMapper;
	private final RestTemplate habrRestTemplate;
	private final String abbrTag = "class=\"habraabbr\"";

	public Set<String> getNewsFromRss() {
		RssFeed object = habrRestTemplate.getForObject("/rss/news/?limit=100", RssFeed.class);
		return rssMapper.map(object.getPosts());
	}

	public Set<String> getPostsFromRss() {
		RssFeed object = habrRestTemplate.getForObject("/rss/all/?limit=100", RssFeed.class);
		return rssMapper.map(object.getPosts());
	}

	public boolean isPostHasABBR(String url) {
		try {
			String pageContent = habrRestTemplate.getForObject(URI.create(url), String.class);
			return pageContent.contains(abbrTag);
		} catch (Exception e) {
			return false;
		}
	}

	public int getCountABBRs(String url) {
		try {
			String pageContent = habrRestTemplate.getForObject(URI.create(url), String.class);
			return Math.abs(pageContent.split(abbrTag).length - 1);
		} catch (Exception e) {
			return 0;
		}
	}
}

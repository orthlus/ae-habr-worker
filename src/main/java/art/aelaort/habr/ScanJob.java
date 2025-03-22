package art.aelaort.habr;

import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScanJob implements ApplicationRunner {
	private final HabrClient habrClient;
	private final HabrTelegram habrTelegram;
	private final HabrStorage habrStorage;

	public void scanNewPosts() {
		Set<String> posts = habrClient.getPostsFromRss();
		Set<String> news = habrClient.getNewsFromRss();
		Set<String> lastPosts = habrStorage.getLastRssPosts();
		Set<String> lastNews = habrStorage.getLastRssNews();

		Sets.SetView<String> newPosts = Sets.difference(posts, lastPosts);
		Sets.SetView<String> newNews = Sets.difference(news, lastNews);

		newPosts.stream()
				.map(url -> new HabrUrlCount(url, habrClient.getCountABBRs(url)))
				.filter(habrUrlCount -> habrUrlCount.count() > 0)
				.map(this::buildMessage)
				.forEach(habrTelegram::sendChannelMessage);
		newNews.stream()
				.map(url -> new HabrUrlCount(url, habrClient.getCountABBRs(url)))
				.filter(habrUrlCount -> habrUrlCount.count() > 0)
				.map(this::buildMessage)
				.forEach(habrTelegram::sendChannelMessage);

		habrStorage.saveLastRssPosts(posts);
		habrStorage.saveLastRssNews(news);
		log.info("habr finish scan, {} new posts, {} new news", newPosts.size(), newNews.size());
	}

	private String buildMessage(HabrUrlCount habrUrlCount) {
		return "%s\n%d abbrs".formatted(habrUrlCount.url(), habrUrlCount.count());
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		scanNewPosts();
	}
}

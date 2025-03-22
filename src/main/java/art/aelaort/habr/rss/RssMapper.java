package art.aelaort.habr.rss;

import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class RssMapper {
	public String map(ItemDto dtoObject) {
		return dtoObject.getGuid();
	}

	public Set<String> map(List<ItemDto> dtoObjects) {
		if ( dtoObjects == null ) {
			return null;
		}

		Set<String> set = new LinkedHashSet<String>( Math.max((int) (dtoObjects.size() / .75f ) + 1, 16 ) );
		for ( ItemDto itemDto : dtoObjects ) {
			set.add( map( itemDto ) );
		}

		return set;
	}
}

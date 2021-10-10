package com.github.demidko.aot;

import static com.github.demidko.aot.WordformMeaning.lookupForMeanings;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;


class WordformMeaningTest {

  @Test
  void lookupForMeaningsTest() throws IOException {
    List<WordformMeaning> meanings = lookupForMeanings("замок");

    for (WordformMeaning x : meanings) {
      System.out.println(x.getLemma().getId() + " -> " + x + x.getMorphology());
    }

    /*
     * замок может означать
     * 1. замок-строение (увидел что? - средневековый замок)
     * 2. замок-запор (увидел что? - замок на двери)
     * 3. замок-строение (купил кого? - средневековый замок)
     * 4. замок-замор (купил кого? - замок на дверь)
     * 5. замок-промок (что случилось? - замок под дождем)
     */
    assertThat(meanings.size(), equalTo(5));

    assertThat(
      meanings.get(0)
        .getLemma()
        .toString(),
      equalTo("замок")
    );

    assertThat(
      meanings.get(4)
        .getLemma()
        .toString(),
      equalTo("замокнуть")
    );

    assertThat(
      meanings.get(0)
        .getTransformations()
        .stream()
        .map(WordformMeaning::toString)
        .collect(toList()),
      // По два слова на каждый дублирующийся смысл с одинаковыми словоформами и одинаковой исходной леммой (замок-строение и замок-устройство)
      // для словаря они являются абсолютно идентичными (так как дублируются не только все формы, но и все падежи, спряжения).
      // Такие случаи предлагается разруливать самостоятельно, это должно быть несложно за счет дублирования.
      // Решение для таких случаев намеренно не вносится в словарь, так как они очень редкие, и их решение уменьшило бы скорость работы
      hasItems("замок", "замок", "замка", "замка", "замку", "замку", "замок", "замок", "замком", "замком", "замке",
        "замке", "замки", "замки", "замков", "замков", "замкам", "замкам", "замки", "замки", "замками", "замками",
        "замках", "замках"
      )
    );

    // а вот если для двух смыслов отличается хотя бы одна словоформа или хотя бы одна морфология, то словарь их разделит
    assertThat(
      meanings.get(4)
        .getTransformations()
        .stream()
        .map(WordformMeaning::toString)
        .collect(toList()),
      hasItems("замокнуть", "замок", "замокла", "замокло", "замокли", "замокну", "замокнем",
        "замокнешь", "замокнете", "замокнет", "замокнут", "замокнув", "замокши", "замокнем", "замокнемте",
        "замокни", "замокните", "замокший", "замокшего", "замокшему", "замокшего", "замокший", "замокшим",
        "замокшем", "замокшая", "замокшей", "замокшей", "замокшую", "замокшей", "замокшею", "замокшей",
        "замокшее", "замокшего", "замокшему", "замокшее", "замокшим", "замокшем", "замокшие", "замокших",
        "замокшим", "замокших", "замокшие", "замокшими", "замокших"
      )
    );
  }
}
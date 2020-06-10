package pl.coderslab.unit;

import org.junit.Test;
import pl.coderslab.service.RestPredictions;

import static org.assertj.core.api.Assertions.assertThat;

public class RestPredictionsTest {

    @Test
    public void convertNewlineCharacterToHTMLBreakTag() {
        //given
        String text = "line1\rline2\n";

        //when
        String actual = RestPredictions.convertNewlineCharacterToHTMLBreakTag(text);
        String expected = "line1<br>line2<br>";

        //then
        assertThat(actual).isEqualTo(expected);
    }
}
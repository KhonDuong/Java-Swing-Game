import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;

public class StatsLabel extends JLabel
{
    StatsLabel(Integer x, Integer y, Integer width, Integer height, String text)
    {
        this.setBounds(x, y, width, height);
        this.setBorder(new EmptyBorder(0, 10, 0, 0));
        this.setVerticalAlignment(JLabel.CENTER);
        this.setHorizontalAlignment(JLabel.LEFT);
        this.setOpaque(true);
        this.setBackground(Color.red);
        updateLabel(text);
    }

    public void updateLabel(String updatedText)
    {
        this.setText(updatedText);
    }
}

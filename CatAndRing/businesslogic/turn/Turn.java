package businesslogic.turn;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public abstract class Turn {



        public abstract boolean isExhausted();
        public abstract int getStartTime();
        public abstract int getEndTime();
        public abstract int TimeLeft();
        public abstract void setTurnExhausted(boolean flag);
        public abstract int getId();

}

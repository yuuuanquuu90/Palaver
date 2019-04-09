package de.paluno.mse.palaver;

import org.junit.Test;

import java.util.ArrayList;

import de.paluno.mse.palaver.appdata.Category;
import de.paluno.mse.palaver.appdata.Message;
import de.paluno.mse.palaver.appdata.MyData;
import de.paluno.mse.palaver.generalhelper.CharacterHelper;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        CharacterHelper c = new CharacterHelper();
    }

    @Test
    public void stringdemo() {
        String a = "";
        String[] aa = a.split(",");
    }

    @Test
    public void conversation() {
        demo d = new demo();
        d.insertItem(new Message("li", "1990-01-02T12:12:12", "hello", true));
        d.insertItem(new Message("li", "1990-01-02T12:12:11", "hello", true));
        d.insertItem(new Message("li", "1990-01-02T12:12:13", "hello", true));
        d.insertItem(new Message("li", "1990-01-02T12:12:14", "hello", true));
        d.insertItem(new Message("li", "1990-01-02T12:12:15", "hello", false));
        d.insertItem(new Message("li", "1990-01-02T12:12:16", "hello", false));
        d.insertItem(new Message("li", "1990-02-02T12:12:16", "hello", false));
        d.insertItem(new Message("li", "1991-02-02T12:12:16", "hello", false));
        d.insertItem(new Message("li", "1912-02-02T12:12:16", "hello", false));
        d.insertItem(new Message("li", "1994-02-02T12:12:16", "hello", false));
        for (Category c : d.mCategory)
            for (int i = 0; i < c.getListSize(); i++)
                if (c.getItem(i).getType() != MyData.TYPE_CATEGORY)
                    System.out.println(((Message) c.getItem(i)).getTimeToSecond() + ((Message) c.getItem(i)).getMessage());
                else
                    System.out.println(c.getItem(i).getName());
    }

    class demo {
        private ArrayList<Category> mCategory;
        private ArrayList<Message> sending;

        demo() {
            mCategory = new ArrayList<>();
        }

        public int getItemCount() {
            int count = 0;
            for (Category c : mCategory) {
                if (c.getExist())
                    count += c.getListSize();
            }
            return count;
        }

        //NOTE: first item in category is automatic this category self.
        private void insertItem(Message msg) {
            String day = msg.getDay();
            String time = msg.getTimeToSecond();
            //insert category
            if (mCategory.size() == 0) {
                // category=null
                mCategory.add(new Category(day, MyData.TYPE_CHAT));
            } else {
                //category !=null
                //this day is the last day
                if (day.compareToIgnoreCase(mCategory.get(mCategory.size() - 1).getName()) > 0) {
                    mCategory.add(new Category(day, MyData.TYPE_CHAT));
                } else {
                    //this day is not the last day
                    for (int i = 0; i < mCategory.size(); i++) {
                        //day is found
                        if (mCategory.get(i).getName().equals(day)) {
                            break;
                        } else if (day.compareToIgnoreCase(mCategory.get(i).getName()) < 0) {
                            mCategory.add(i, new Category(day, MyData.TYPE_CHAT));
                            break;
                        }

                    }
                }

            }
            //catch the day
            Category ca = new Category("-1");
            for (Category c : mCategory) {
                if (c.getName().equals(day)) {
                    ca = c;
                    break;
                }
            }
            //insert item
            //last time
            if (!ca.getExist() || time.compareToIgnoreCase(((Message) ca.getItem(ca.getListSize() - 1)).getTimeToSecond()) > 0) {
                ca.addMsg(msg);
            } else {
                for (int i = 1; i < ca.getListSize(); i++) {
                    if (time.compareToIgnoreCase(((Message) ca.getItem(i)).getTime()) < 0) {
                        ca.addMsg(i, msg);
                        break;
                    }
                }
            }
        }
    }
}
/**
 * @Author: nanaya
 * @Date: 2024/07/21/5:52
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
import java.util.ArrayList;
import java.util.List;

public class ListContainsInFieldExample {
    public static void main(String[] args) {
        // 创建并初始化一个Person对象的List
        List<Person> people = new ArrayList<>();
        people.add(new Person("Alice"));
        people.add(new Person("Bob"));
        people.add(new Person("Charlie"));

        // 要检查的名字
        String nameToCheck = "Bob";

        // 使用流API检查nameToCheck是否存在于Person对象的name字段中
        boolean exists = people.stream()
                .anyMatch(person -> person.getName().equals(nameToCheck));

        // 打印结果
        if (exists) {
            System.out.println(nameToCheck + " 存在于列表中的Person对象的name字段中。");
        } else {
            System.out.println(nameToCheck + " 不存在于列表中的Person对象的name字段中。");
        }
    }
}

// Person类
class Person {
    private String name;

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

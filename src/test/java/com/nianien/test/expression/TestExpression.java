package com.nianien.test.expression;

import com.nianien.core.collection.map.MapWrapper;
import com.nianien.core.text.Expression;
import com.nianien.core.text.Expression.VariableHandler;
import org.junit.Test;

import java.util.*;


/**
 * @author skyfalling
 */
public class TestExpression {


    static class SqlVariableHandler implements VariableHandler {

        private final List parameters = new ArrayList();
        private final Map<String, Object> map = new HashMap<String, Object>();

        public SqlVariableHandler(Object... args) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null) {
                    map.put(String.valueOf(i), args[i]);
                }
            }
        }

        public SqlVariableHandler(Map<String, ?> args) {
            map.putAll(args);
        }

        @Override
        public Object handle(String variable) {
            parameters.add(map.get(variable));
            return "?";
        }
    }

    private void ff(String exp, Object... args) {
        Expression expression = new Expression("{", "}");
        SqlVariableHandler sql = new SqlVariableHandler(args);
        System.out.println(expression.eval(exp, sql));
        ;
        System.out.println(sql.parameters);
    }

    private void ff(String exp, Map args) {
        Expression expression = new Expression("{", "}");
        SqlVariableHandler sql = new SqlVariableHandler(args);
        System.out.println(expression.eval(exp, sql));
        System.out.println(sql.parameters);
    }

    public static void main(String[] args) {
        TestExpression tx = new TestExpression();
        tx.ff("select * from user where user={0} and age={1}", "lining", 19);
        tx.ff("select * from user where user={user} and age={age}", new MapWrapper("user", "lining"));
    }

    @Test
    public void test() throws Exception {

        Expression.defaultExpression.eval("{0}年{1}月{2}日,{2}/{1}/{0}", "2010", "01", "6");
        System.out.println(Expression.defaultExpression.eval("{0}年{1}月{2}日,{0}-{1}-{2}", "2010", "01", "6"));

        System.out.println(String.format("%3$s年%2$s月%1$s日,%3$s-%2$s-%1$s", "6", "01", "2010"));
        Map map = new HashMap();
        map.put("year", 2012);
        map.put("month", 12);
        map.put("day", 21);
        System.out.println(Expression.defaultExpression.eval("{year}年{month}月{day}日", map));

        map.put("0", 2012);
        map.put("1", 12);
        map.put("2", 21);
        map.put("2012", "二〇一二");
        map.put("12", "十二");
        map.put("21", "二十一");
        System.out.println(Expression.defaultExpression.eval("{{0}}年{{1}}月{{2}}日", map));
    }

    @Test
    public void test1() throws Exception {
        Expression expression = new Expression("${", "}");
        Map<String, String> map = new HashMap<String, String>();
        map.put("x", "${a}");
        map.put("a", "[${b}]-[${c}]");
        map.put("b", "${d}+${c}");
        map.put("c", "${d}-${e}");
        map.put("d", "date");
        map.put("e", "test");
        Map testMap = new HashMap();
        for (String key : map.keySet()) {
            String value = map.get(key);
            testMap.put(key, expression.eval(value, map));
        }
        System.out.println(testMap);
        System.out.println(map);
    }


    @Test
    public void test2() {
        Expression exp = new Expression("$", " ");
        Map<String, String> map = new HashMap<String, String>();
        map.put("0", "2010");
        map.put("1", "01");
        map.put("2", "6");
        map.put("01", "2001");
        String source = "今天是$$1  年$1 月$2 日";
        System.out.println(exp.eval(source, "2010", "01", "6"));
        System.out.println(exp.eval(source, map));
    }

}

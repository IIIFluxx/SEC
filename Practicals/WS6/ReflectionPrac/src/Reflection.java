import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Reflection
{
    public static void main(String[] args) {

        try
        {
            Scanner sc = new Scanner(System.in);
            ArrayList<String> list = new ArrayList<>();
            System.out.println("Enter class name");
            String className = sc.nextLine();

            Class<?> c = Class.forName(className);
            // Checks if constructor exists, specifically one that takes in a String.
            Constructor con = c.getConstructor(new Class[]{String.class});

            // Works ^.

            System.out.println("Enter constructor String");
            String conName = sc.nextLine();
            // Retrieve new obj of this class
            Object obj = (Object)con.newInstance(conName);

            // (c) Output list of all non-static methods within the class that take a single int as a parameter.

            for(Method m : c.getMethods())
            {
                //System.out.println("Check: " + m.getName()); // TEST: Works.
                // Check if method 'm' takes in a single int as a parameter
                // Static - getModifiers
                // Single int - getParameterTypes
                Class<?>[] types = m.getParameterTypes();
                // Single integer - 1x length, integer type.
                if(types.length == 1 && types[0].equals(int.class))
                {
                    if (!Modifier.isStatic(m.getModifiers())) // Check if method is not static
                    {
                        list.add(m.getName());
                    }
                }
            } // End for
            System.out.println(list.toString()); // Display each method.

            // =================================
            // (d) Ask the user to select a method from the list.
            String methodChoice;
            int paramChoice = -1;
            do
            {
                System.out.println("Select method from above list:");
                methodChoice = sc.nextLine();
            } while(!list.contains(methodChoice));
            // Ask them to enter a value for the parameter,
            boolean cont = false;
            do
            {
                try
                {
                    System.out.println("Select int parameter for chosen method:");
                    paramChoice = sc.nextInt();
                    cont = true;
                } catch (NumberFormatException | InputMismatchException ex) {
                    System.out.println("Please enter a valid integer!");
                    sc.next();
                }
            } while(cont == false);
            // =================================


            // & then call the method with that value
            Method method = c.getMethod(methodChoice, int.class);
            Object returnValue = method.invoke(obj, paramChoice);

            System.out.println("Output: " + returnValue.toString());
        }
        catch (ClassNotFoundException e)
        {
            // Constructor doesn't exist.
            System.out.println("Class not found.");
        }
        catch (NoSuchMethodException e)
        {
            // Method doesn't exist.
            System.out.println("Method not found.");
        }
        /*catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }*/
        catch(ReflectiveOperationException e)
        {
            System.out.println("An ERROR has occurred: " + e.getMessage());
        }
    }


}

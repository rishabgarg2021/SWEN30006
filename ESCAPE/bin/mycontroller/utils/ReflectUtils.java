package mycontroller.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created for SWEN30006 - Project Part C (Learning to Escape)
 * <p>
 * Class providing various utility methods for use in other classes.
 *
 * @author Project Group 63 (Dennis Goyal, Rishab Garg, Thomas Fowler)
 */
public class ReflectUtils {

	private ReflectUtils() {
	}

	/**
	 * Finds and invokes the constructor of the class matching the passed name, with the specified arguments passed to it.
	 *
	 * @param name Name of the class to create a new instance of.
	 * @param args Arguments to pass to the class's constructor.
	 * @param <T>  Type to case the result to.
	 * @return A new instance of the class, constructed with the given parameters.
	 * @throws ClassNotFoundException    If the given class name cannot be resolved to a class.
	 * @throws NoSuchMethodException     If there is no constructor defined on the class.
	 * @throws IllegalAccessException    If the constructor in inaccessible from this class.
	 * @throws InvocationTargetException If there is an error when invoking the constructor
	 * @throws InstantiationException    If  there is an error when invoking the constructor
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> T newInstance(String name, Object... args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Class<?> clazz = Class.forName(name);

		Constructor<?> ctor = null;
		Constructor<?>[] constructors = clazz.getDeclaredConstructors();

		for (Constructor<?> contructor : constructors) {
			Class<?>[] types = contructor.getParameterTypes();

			if (types.length == args.length) {
				for (int i = 0; i < types.length; i++) {
					if (!types[i].isInstance(args[i]))
						break;
				}

				ctor = contructor;
			}

		}

		if(null == ctor)
			throw new RuntimeException("No valid constructor was found.");

		return (T) ctor.newInstance(args);
	}
}

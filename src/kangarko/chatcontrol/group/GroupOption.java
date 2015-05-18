package kangarko.chatcontrol.group;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import kangarko.chatcontrol.config.ConfHelper.ChatMessage;

public class GroupOption {

	private final Option option;
	private final Object value;

	private GroupOption(Option option, Object value) {
		this.option = option;
		this.value = value;
	}

	public Option getOption() {
		return option;
	}

	public Object getValue() {
		return value;
	}

	public static enum Option {
		MESSAGE_DELAY(Integer.class),
		COMMAND_DELAY(Integer.class),

		JOIN_MESSAGE(ChatMessage.class),
		LEAVE_MESSAGE(ChatMessage.class),
		KICK_MESSAGE(ChatMessage.class);

		private final Class<?> validValue;
		private final String toString;

		Option(Class<?> validValue) {
			this.validValue = validValue;
			this.toString = makeString();
		}

		public <T> GroupOption create(T valueRaw) {
			Object value = valueRaw;
			
			if (!value.getClass().isAssignableFrom(validValue)) {
				value = null;

				try {
					value = Integer.parseInt(String.valueOf(valueRaw));			
				} catch (Exception ex) {
				}
				try {
					value = Double.parseDouble(String.valueOf(valueRaw));
				} catch (Exception ex) {
				}
				try {
					value = valueRaw.equals("true") || valueRaw.equals("false") ? Boolean.parseBoolean(String.valueOf(valueRaw)) : null;
				} catch (Exception ex) {
				}

				if (value == null)
					value = valueRaw;
			}

			checkValid(value);
			
			return new GroupOption(this, validValue == ChatMessage.class && valueRaw.getClass() != ChatMessage.class ? new ChatMessage(String.valueOf(value)) : value);
		}


		public static Option parseOption(String name) {			
			for (Option type : values())
				if (type.name().equalsIgnoreCase(name) || type.toString().equalsIgnoreCase(name))
					return type;

			throw new RuntimeException("Unknown group setting: '" + name + "', use one of these: " + StringUtils.join(values(), ", "));
		}

		protected void checkValid(Object value) {
			if (validValue == ChatMessage.class) {
				// all valid
			} else
				Validate.isTrue(value.getClass().isAssignableFrom(validValue), this + " has to be " + validValue.getSimpleName() + "! (got " + value + ")");
		}

		private String makeString() {
			String[] splitted = name().toLowerCase().split("_");
			String nazov = "";

			for (String part : splitted)
				nazov = nazov + (nazov.isEmpty() ? "" : "_") + part.substring(0, 1).toUpperCase() + part.substring(1);

			return nazov;
		}

		@Override
		public String toString() {
			return toString;
		}
	}
}
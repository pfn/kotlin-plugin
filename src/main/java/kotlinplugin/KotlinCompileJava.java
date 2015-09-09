package kotlinplugin;

import org.jetbrains.kotlin.cli.common.ExitCode;
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments;
import org.jetbrains.kotlin.cli.common.messages.MessageCollector;
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler;
import org.jetbrains.kotlin.config.Services;

/**
 * @author pfnguyen
 */
public class KotlinCompileJava {
    public static ExitCode compile(MessageCollector c,
                               K2JVMCompiler compiler,
                               K2JVMCompilerArguments args) {
        return compiler.exec(c, Services.EMPTY, args);
    }
}

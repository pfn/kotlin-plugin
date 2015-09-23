package kotlinplugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.cli.common.ExitCode;
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments;
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation;
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity;
import org.jetbrains.kotlin.cli.common.messages.MessageCollector;
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler;
import org.jetbrains.kotlin.config.Services;
import sbt.Logger;
import sbt.MessageOnlyException;
import scala.Function0;
import xsbti.F0;

/**
 * @author pfnguyen
 */
public class KotlinCompileJava {
    public static void compile(Logger log,
                               K2JVMCompilerArguments args) {
        ExitCode r = new K2JVMCompiler().exec(new CompilerLogger(log), Services.EMPTY, args);
        switch (r) {
            case COMPILATION_ERROR:
            case INTERNAL_ERROR:
                throw new MessageOnlyException("Compilation failed. See log for more details");
        }
    }
    static class CompilerLogger implements MessageCollector {
        private final Logger log;
        public CompilerLogger(Logger log) {
            this.log = log;
        }
        @Override public void report(@NotNull CompilerMessageSeverity severity,
                                     @NotNull String message,
                                     @NotNull CompilerMessageLocation location) {
            String m = message;
            String path = location.getPath();
            if (path != null)
                m = path + ": " + location.getLine() + ", " + location.getColumn() + ": " + message;
            switch (severity) {
                case INFO: log.info(msg(m));
                    break;
                case WARNING: log.warn(msg(m));
                    break;
                default:
                    if (CompilerMessageSeverity.ERRORS.contains(severity)) log.error(msg(m));
                    else if (CompilerMessageSeverity.VERBOSE.contains(severity)) log.debug(msg(m));
                    break;

            }
        }
    }

    static F0<String> msg(final String m) {
        return new F0<String>() {
            @Override
            public String apply() {
                return m;
            }
        };
    }
}

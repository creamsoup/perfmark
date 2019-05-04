package io.grpc.contrib.perfmark;

import com.google.errorprone.annotations.CompileTimeConstant;

public abstract class PerfMarkCloseable implements AutoCloseable {

    @Override
    public abstract void close();

    static final PerfMarkCloseable NOOP = new NoopAutoCloseable();

    PerfMarkCloseable() {}

    private static final class NoopAutoCloseable extends PerfMarkCloseable {
        @Override
        public void close() {}

        NoopAutoCloseable() {}
    }

    static final class TaskTagAutoCloseable extends PerfMarkCloseable {
        private final String taskName;
        private final Tag tag;

        @Override
        public void close() {
            PerfMark.stopTask(taskName, tag);
        }

        TaskTagAutoCloseable(@CompileTimeConstant String taskName, Tag tag) {
            this.taskName = taskName;
            this.tag = tag;
        }
    }

    static final class TaskAutoCloseable extends PerfMarkCloseable {
        private final String taskName;

        @Override
        public void close() {
            PerfMark.stopTask(taskName);
        }

        TaskAutoCloseable(@CompileTimeConstant String taskName) {
            this.taskName = taskName;
        }
    }

    static final class MarkerTagAutoCloseable extends PerfMarkCloseable {
        private final Marker marker;
        private final Tag tag;

        @Override
        public void close() {
            PerfMark.Package.Access.stopTask(marker, tag);
        }

        MarkerTagAutoCloseable(Marker marker, Tag tag) {
            this.marker = marker;
            this.tag = tag;
        }
    }

    static final class MarkerAutoCloseable extends PerfMarkCloseable {
        private final Marker marker;

        @Override
        public void close() {
            PerfMark.Package.Access.stopTask(marker);
        }

        MarkerAutoCloseable(Marker marker) {
            this.marker = marker;
        }
    }
}


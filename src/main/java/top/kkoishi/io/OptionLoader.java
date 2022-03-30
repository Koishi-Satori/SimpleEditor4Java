package top.kkoishi.io;

import top.kkoishi.lang.PropertiesLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

/**
 * @author KKoishi_
 */
public final class OptionLoader {

    public static void main (String[] args) throws IOException, PropertiesLoader.IllegalOrBadPropertyFormatException {
        final OptionLoader loader = new OptionLoader();
        loader.load(new File("./data/extensions.en"));
        loader.translate();
        System.out.println(loader.getOptions());
    }

    /**
     * Load from custom proc.
     * <pre>
     *     example:
     *     YAML File=yml,yaml
     * this will be load to
     * Option{info="YAML File", extensions=["yml", "yaml"]}
     * </pre>
     *
     * @author KKoishi_
     */
    private static final class DefaultLoader extends PropertiesLoader<FileChooser.Option> {

        private String[] lines;

        private int pos = -1;

        private void flushLines () {
            final LinkedList<String> lines = new LinkedList<>();
            StringBuilder sb = new StringBuilder();
            for (final char c : in.toCharArray()) {
                if (c == '\r') {
                    continue;
                }
                if (c == '\n') {
                    if (!sb.isEmpty()) {
                        lines.add(sb.toString());
                        sb = new StringBuilder();
                    }
                } else {
                    sb.append(c);
                }
            }
            this.lines = new String[lines.size()];
            int i = 0;
            while (!lines.isEmpty()) {
                this.lines[i++] = lines.removeFirst();
            }
        }

        private String[] parseLine (String line) throws IllegalOrBadPropertyFormatException {
            final LinkedList<String> elements = new LinkedList<>();
            boolean indexingExtension = false;
            StringBuilder sb = new StringBuilder();
            for (final char c : line.toCharArray()) {
                if (indexingExtension) {
                    if (c == ',') {
                        elements.add(sb.toString());
                        sb = new StringBuilder();
                    } else {
                        sb.append(c);
                    }
                } else {
                    if (c == '=') {
                        indexingExtension = true;
                        elements.add(sb.toString());
                        sb = new StringBuilder();
                    } else {
                        sb.append(c);
                    }
                }
            }
            if (!sb.isEmpty()) {
                elements.add(sb.toString());
            }
            if (elements.isEmpty()) {
                throw new IllegalOrBadPropertyFormatException();
            }
            final String[] res = new String[elements.size()];
            int i = 0;
            while (!elements.isEmpty()) {
                res[i++] = elements.removeFirst();
            }
            return res;
        }

        @Override
        public void translate () throws IllegalOrBadPropertyFormatException {
            flushLines();
            super.translate();
            pos = -1;
        }

        @Override
        protected FileChooser.Option getInstance (Object... params) {
            final String[] extensions = new String[params.length - 1];
            for (int i = 1; i < params.length; i++) {
                extensions[i - 1] = (String) params[i];
            }
            return FileChooser.buildOption((String) params[0], extensions);
        }

        @Override
        protected boolean hasNext () {
            return pos + 1 < lines.length;
        }

        @Override
        protected Object[] next () throws IllegalOrBadPropertyFormatException {
            return parseLine(lines[++pos]);
        }
    }

    public OptionLoader () {
        this(StandardCharsets.UTF_8);
    }

    public OptionLoader (Charset charset) {
        this(new DefaultLoader(), charset);
    }

    private OptionLoader (final PropertiesLoader<FileChooser.Option> formatter) {
        this(formatter, StandardCharsets.UTF_8);
    }

    private OptionLoader (PropertiesLoader<FileChooser.Option> formatter, Charset charset) {
        this.formatter = formatter;
        this.charset = charset;
    }

    private final PropertiesLoader<FileChooser.Option> formatter;

    private String content;

    private Charset charset;

    public void load (InputStream in) throws IOException {
        load(read(in));
    }

    public void load (Reader reader) {

    }

    public void load (String proc) {
        this.content = proc;
    }

    public void load (File file) throws IOException {
        load(new FileInputStream(file));
    }

    public String read (InputStream in) throws IOException {
        int len;
        final byte[] buffer = new byte[1024];
        final StringBuilder sb = new StringBuilder();
        while ((len = in.read(buffer)) != -1) {
            sb.append(new String(buffer, 0, len, charset));
        }
        return sb.toString();
    }

    public PropertiesLoader<FileChooser.Option> getFormatter () {
        return formatter;
    }

    public Charset getCharset () {
        return charset;
    }

    public void setCharset (Charset charset) {
        this.charset = charset;
    }

    public void translate () throws PropertiesLoader.IllegalOrBadPropertyFormatException {
        formatter.load(content);
        formatter.translate();
    }

    public List<FileChooser.Option> getOptions () {
        return formatter.getResult();
    }

    public static FileChooser.Option[] list2array (List<FileChooser.Option> options) {
        return options.toArray(FileChooser.Option[]::new);
    }
}

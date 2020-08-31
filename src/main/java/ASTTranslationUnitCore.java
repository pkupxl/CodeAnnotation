import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.parser.IScannerExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.c.ANSICParserExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.c.GCCParserExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.c.GCCScannerExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.c.ICParserExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.cpp.ANSICPPParserExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.cpp.GPPParserExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.cpp.GPPScannerExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.cpp.ICPPParserExtensionConfiguration;
import org.eclipse.cdt.core.parser.*;
import org.eclipse.cdt.internal.core.dom.parser.AbstractGNUSourceCodeParser;
import org.eclipse.cdt.internal.core.dom.parser.c.GNUCSourceParser;
import org.eclipse.cdt.internal.core.dom.parser.cpp.GNUCPPSourceParser;
import org.eclipse.cdt.internal.core.parser.scanner.CPreprocessor;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ASTTranslationUnitCore {
    private final static IParserLogService NULL_LOG = new NullLogService();
    public IASTTranslationUnit parse(
            String file,
            ParserLanguage parserLanguage,
            boolean useGNUExtensions,
            boolean skipTrivialInitializers
    ) {
        IScanner scanner = null;
        try {
            scanner = createScanner(
                    FileContent.create(file, FileUtils.readFileToString(new File(file)).toCharArray()),
                    parserLanguage,
                    ParserMode.COMPLETE_PARSE,
                    createScannerInfo(useGNUExtensions)
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        AbstractGNUSourceCodeParser gnuSourceCodeParser = null;
        if (parserLanguage == ParserLanguage.CPP) {
            ICPPParserExtensionConfiguration configuration = useGNUExtensions ?
                    new GPPParserExtensionConfiguration() :
                    new ANSICPPParserExtensionConfiguration();
            gnuSourceCodeParser = new GNUCPPSourceParser(
                    scanner, ParserMode.COMPLETE_PARSE, NULL_LOG, configuration, null
            );
        }
        else {
            ICParserExtensionConfiguration configuration = useGNUExtensions ?
                    new GCCParserExtensionConfiguration():
                    new ANSICParserExtensionConfiguration();
            gnuSourceCodeParser = new GNUCSourceParser(
                    scanner, ParserMode.COMPLETE_PARSE, NULL_LOG, configuration, null
            );
        }
        return gnuSourceCodeParser.parse();
    }

    private IScanner createScanner(
            FileContent fileContent,
            ParserLanguage parserLanguage,
            ParserMode parserMode,
            IScannerInfo scannerInfo
    ) {
        IScannerExtensionConfiguration configuration =
                parserLanguage == ParserLanguage.C ?
                        GCCScannerExtensionConfiguration.getInstance(scannerInfo) :
                        GPPScannerExtensionConfiguration.getInstance(scannerInfo);
        // garcia.wul 最后一个参数：IncludeFileContentProvider传null，不然会报workspace is closed错误
        return new CPreprocessor(
                fileContent, scannerInfo, parserLanguage, NULL_LOG, configuration, null
        );
    }

    public ScannerInfo createScannerInfo(boolean useGNUExtensions) {
        return useGNUExtensions ? new ScannerInfo(getGnuMap()) : new ScannerInfo(getStdMap());
    }

    private Map<String, String> getGnuMap() {
        Map<String, String> map= Maps.newHashMap();
        map.put("__GNUC__", "4");
        map.put("__GNUC_MINOR__", "7");
        map.put("__SIZEOF_SHORT__", "2");
        map.put("__SIZEOF_INT__", "4");
        map.put("__SIZEOF_LONG__", "8");
        map.put("__SIZEOF_POINTER__", "8");
        return map;
    }

    private Map<String, String> getStdMap() {
        Map<String, String> map= Maps.newHashMap();
        map.put("__SIZEOF_SHORT__", "2");
        map.put("__SIZEOF_INT__", "4");
        map.put("__SIZEOF_LONG__", "8");
        map.put("__SIZEOF_POINTER__", "8");
        return map;
    }
}

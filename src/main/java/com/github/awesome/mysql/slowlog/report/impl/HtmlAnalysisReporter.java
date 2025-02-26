package com.github.awesome.mysql.slowlog.report.impl;

import com.github.awesome.mysql.slowlog.analyzer.model.AnalysisResult;
import com.github.awesome.mysql.slowlog.config.Config;
import com.github.awesome.mysql.slowlog.report.AnalysisReporter;
import lombok.AllArgsConstructor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

@AllArgsConstructor
public class HtmlAnalysisReporter implements AnalysisReporter {

    private static final String HTML_TEMPLATE_NAME = "mysql-slow-log-analysis-report-template.html";

    private static final String HTML_TEMPLATE_SUFFIX = ".html";

    private static final String I18N_BASE_NAME = "html_analysis_report_i18n";

    private final Config config;

    @Override
    public void report(AnalysisResult result) throws IOException {
        ResourceBundle i18nChinese = ResourceBundle.getBundle(I18N_BASE_NAME, Locale.CHINA);
        ResourceBundle i18nEnglish = ResourceBundle.getBundle(I18N_BASE_NAME, Locale.US);
        renderHtml(result, i18nChinese);
        renderHtml(result, i18nEnglish);
    }

    private void renderHtml(AnalysisResult result, ResourceBundle i18n) throws IOException {
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        templateResolver.setSuffix(HTML_TEMPLATE_SUFFIX);
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        setNoLocaleVariables(context, result);
        setLocaleVariables(context, i18n);

        final String html = templateEngine.process(HtmlAnalysisReporter.HTML_TEMPLATE_NAME, context);
        final String i18nSuffix = String.format("%s_%s", i18n.getLocale().getLanguage(), i18n.getLocale().getCountry());
        final String reportName = HtmlAnalysisReporter.HTML_TEMPLATE_NAME.replace("template", i18nSuffix);

        Files.write(Paths.get(config.getAnalysisReportPath(), reportName), html.getBytes(StandardCharsets.UTF_8));
    }

    private void setNoLocaleVariables(Context context, AnalysisResult result) {
        context.setVariable("totalSlowQueries", result.getTotalSlowQueries());
        context.setVariable("slowestQueryTimeMillis", result.getSlowestQuery().getQueryTimeMillis().toPlainString());
        context.setVariable("longestLockTimeMillis", result.getLongestLockTimeQuery().getLockTimeMillis().toPlainString());
        context.setVariable("maxRowsSent", result.getMaxRowsSentQuery().getRowsSent());
        context.setVariable("maxRowsExamined", result.getMaxRowsExaminedQuery().getRowsExamined());
        context.setVariable("minRowsEfficiency",
            String.format("%d/%d",
                result.getMinRowsEfficiencyQuery().getRowsSent(),
                result.getMinRowsEfficiencyQuery().getRowsExamined()
            )
        );
        context.setVariable("topSlowQueries", result.getTopSlowQueries());
    }

    private void setLocaleVariables(Context context, ResourceBundle i18n) {
        Set<String> i18nKeys = i18n.keySet();
        for (String key : i18nKeys) {
            context.setVariable(key, i18n.getString(key));
        }
        // parameterized messages
        final String topSlowQueriesTableTitle = MessageFormat.format(i18n.getString("topSlowQueriesTableTitle"), config.getTopSlowQueries());
        context.setVariable("topSlowQueriesTableTitle", topSlowQueriesTableTitle);
    }

}

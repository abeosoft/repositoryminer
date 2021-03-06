package org.repositoryminer.metrics.codemetric;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.repositoryminer.metrics.ast.AST;
import org.repositoryminer.metrics.ast.AbstractMethod;
import org.repositoryminer.metrics.ast.AbstractType;
import org.repositoryminer.metrics.report.ClassReport;
import org.repositoryminer.metrics.report.FileReport;
import org.repositoryminer.metrics.report.MethodReport;
import org.repositoryminer.metrics.report.ProjectReport;

public class LOC extends CodeMetric {

	private Pattern pattern;

	public LOC() {
		super.id = CodeMetricId.LOC;
		pattern = Pattern.compile("(\r\n)|(\n)|(\r)");
	}

	@Override
	public void calculate(AST ast, FileReport fileReport, ProjectReport projectReport) {
		fileReport.getMetricsReport().setCodeMetric(CodeMetricId.LOC, calculate(ast.getSource()));
		for (AbstractType type : ast.getTypes()) {
			ClassReport cr = fileReport.getClass(type.getName());
			cr.getMetricsReport().setCodeMetric(CodeMetricId.LOC, calculate(ast, type));
			for (AbstractMethod method : type.getMethods()) {
				MethodReport mr = cr.getMethodBySignature(method.getName());
				mr.getMetricsReport().setCodeMetric(CodeMetricId.LOC, calculate(ast, method));
			}
		}
	}

	public int calculate(AST ast, AbstractType type) {
		String clazz = ast.getSource().substring(type.getStartPosition(), type.getEndPosition());
		String body = clazz.substring(clazz.indexOf('{'));
		return calculate(body);
	}

	public int calculate(AST ast, AbstractMethod method) {
		String m = ast.getSource().substring(method.getStartPosition(), method.getEndPosition());
		return m.contains("{") ? calculate(m.substring(m.indexOf('{'))) : 0;
	}

	public int calculate(String source) {
		if (source == null || source.length() == 0) {
			return 0;
		}

		int lines = 1;
		Matcher matcher = pattern.matcher(source);
		while (matcher.find()) {
			lines++;
		}

		return lines;
	}

	@Override
	public void clean(ProjectReport projectReport) {
	}

}
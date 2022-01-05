package processor;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * 具体语法参见：
 *      https://gitee.com/alsd51/Doraemon/blob/master/aboutjava/src/main/java/aboutjava/annotion/combat/CombatJCTreeProcessor.java
 *      https://segmentfault.com/a/1190000022157161
 */
@SupportedAnnotationTypes(value = {"processor.PackageAnnotation"})
@SupportedSourceVersion(value = SourceVersion.RELEASE_8)
public class AnnotationProcessor extends AbstractProcessor {

    private Messager messager;
    private JavacTrees trees;
    private TreeMaker treeMaker;
    private Names names;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> packages = roundEnv.getElementsAnnotatedWith(PackageAnnotation.class);
        Set<String> packageNames = new HashSet<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(PackageAnnotation.class)) {
            if(ElementKind.PACKAGE.equals(element.getKind())){
                Symbol.PackageSymbol packageSymbol = (Symbol.PackageSymbol) element;

                packageNames.add(packageSymbol.toString());
            }
        }



        System.out.println("=================process==============");
        for (Element rootElement : roundEnv.getRootElements()) {
            if(ElementKind.CLASS.equals(rootElement.getKind())){
                // 类
                Symbol.ClassSymbol classSymbol = (Symbol.ClassSymbol) rootElement;
                handleClass(classSymbol);

            }
        }
        return true;
    }

    private void handleClass(Symbol.ClassSymbol classSymbol) {
        for (Symbol enclosedElement : classSymbol.getEnclosedElements()) {

            if(ElementKind.METHOD.equals(enclosedElement.getKind())){
                // 方法
                Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) enclosedElement;

                handleMethod(methodSymbol);
            }
        }
    }

    private void handleMethod(Symbol.MethodSymbol methodSymbol) {
        JCTree jcTree = trees.getTree(methodSymbol);
        jcTree.accept(new TreeTranslator() {
            @Override
            public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl){
                super.visitMethodDef(jcMethodDecl);

                String methodName = MessageFormat.format("{0}:{1}", methodSymbol.owner.toString(), methodSymbol.toString());
                if(!"jsr269.user.impl.User:test()".equals(methodName)){
                    return;
                }
                jcMethodDecl.getBody().stats = modifyStatements(methodName,null,jcMethodDecl.getBody().getStatements());
                result = jcMethodDecl;
            }

        });
    }

    private List<JCTree.JCStatement> modifyStatements(String preName,String attr, List<JCTree.JCStatement> oldStatments){
        ListBuffer<JCTree.JCStatement> listBuffer = new ListBuffer<>();

        // 第一行日志打印
        if(isEndStatement(oldStatments.head)){
            listBuffer.add(getLogSingleStatement(preName,attr,oldStatments.head.toString()));
        }else{
            listBuffer.add(getLogStartStatement(preName,attr));
        }

        // 原理逻辑
        for (JCTree.JCStatement oldStatment : oldStatments) {
            listBuffer.add(convert(preName,oldStatment));
        }

        // 最后一行日志打印
        if(!isEndStatement(oldStatments.last())){
            listBuffer.add(getLogEndStatement(preName,attr));
        }


        return listBuffer.toList();
    }

    private boolean isEndStatement(JCTree.JCStatement lastStatment) {
        return lastStatment instanceof JCTree.JCReturn
                || lastStatment instanceof JCTree.JCBreak
                || lastStatment instanceof JCTree.JCContinue;
    }

    private JCTree.JCStatement convert(String preName,JCTree.JCStatement oldStatment) {

        if(oldStatment instanceof JCTree.JCIf){
            JCTree.JCIf ifStatment = (JCTree.JCIf) oldStatment;

            JCTree.JCStatement thenStatement = ifStatment.getThenStatement();
            if(thenStatement instanceof JCTree.JCBlock){
                JCTree.JCBlock blockStatement = (JCTree.JCBlock) thenStatement;
                blockStatement.stats = modifyStatements(preName+":if",ifStatment.cond.toString(),blockStatement.stats);
            }else{
                List<JCTree.JCStatement> newStatement = modifyStatements(preName+":if",ifStatment.cond.toString(),List.of(thenStatement));
                ifStatment.thenpart = treeMaker.Block(0,newStatement);
            }

            JCTree.JCStatement elseStatement = ifStatment.getElseStatement();
            if(elseStatement != null){
                if(elseStatement instanceof JCTree.JCBlock) {

                    JCTree.JCBlock blockStatement = (JCTree.JCBlock) elseStatement;
                    blockStatement.stats = modifyStatements(preName + ":else", ifStatment.cond.toString(), blockStatement.stats);
                }else if(elseStatement instanceof JCTree.JCIf){
                    ifStatment.elsepart = convert(preName,elseStatement);
                }else{
                    List<JCTree.JCStatement> newStatement = modifyStatements(preName + ":else", ifStatment.cond.toString(), List.of(elseStatement));
                    ifStatment.elsepart = treeMaker.Block(0,newStatement);

                }
            }
        }

        return oldStatment;
    }



    private JCTree.JCExpressionStatement getLogStartStatement(String pre,String attr) {
        String msg = MessageFormat.format("excuted {0}{1}", pre, attr != null ? attr : "");
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING,"modify code, add start log:"+pre+attr);
        return getLogStatement(msg);
    }

    private JCTree.JCExpressionStatement getLogSingleStatement(String pre,String attr,String res) {
        String msg = MessageFormat.format("excuted {0}{1} res:{2}", pre, attr != null ? attr : "",res);
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING,"modify code, add single log:"+pre+attr);
        return getLogStatement(msg);
    }

    private JCTree.JCExpressionStatement getLogEndStatement(String pre,String attr) {
        String msg = MessageFormat.format("excuted {0}{1}", pre, attr != null ? attr : "");
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING,"modify code, add end   log:"+pre+attr);
        return getLogStatement(msg);
    }

    private JCTree.JCExpressionStatement getLogStatement(String msg) {
        return treeMaker.Exec(
                    treeMaker.Apply(
                            List.nil(),
                            treeMaker.Select(treeMaker.Select(
                                    treeMaker.Ident(names.fromString("processor")),
                                    names.fromString("MyLog")),
                                    names.fromString("log")
                            ),
                            List.of(treeMaker.Literal(msg))
                    )
            );
    }


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        System.out.println("=================init==============");
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }
}

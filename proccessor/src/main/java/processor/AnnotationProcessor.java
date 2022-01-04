package processor;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * 具体语法参见：
 *      https://gitee.com/alsd51/Doraemon/blob/master/aboutjava/src/main/java/aboutjava/annotion/combat/CombatJCTreeProcessor.java
 *      https://segmentfault.com/a/1190000022157161
 */
@SupportedAnnotationTypes(value = {"processor.MethodAnnotation"})
@SupportedSourceVersion(value = SourceVersion.RELEASE_8)
public class AnnotationProcessor extends AbstractProcessor {

    private Messager messager;
    private JavacTrees trees;
    private TreeMaker treeMaker;
    private Names names;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        System.out.println("=================process==============");
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(MethodAnnotation.class);
        set.forEach(element -> {
            JCTree jcTree = trees.getTree(element);
            jcTree.accept(new TreeTranslator() {
                @Override
                public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl){
                    super.visitMethodDef(jcMethodDecl);
                    JCTree.JCExpressionStatement startStat = treeMaker.Exec(
                            treeMaker.Apply(
                                    List.nil(),
                                    treeMaker.Select(treeMaker.Select(
                                            treeMaker.Ident(names.fromString("processor")),
                                            names.fromString("MyLog")),
                                            names.fromString("log")

                                    ),
                                    List.of(treeMaker.Literal(" start log ?????哈哈哈？？？？？")) // 方法中的内容
                            )
                    );
                    System.out.println("=================visitMethodDef==============");
                    jcMethodDecl.getBody().stats =
                            jcMethodDecl.getBody().getStatements().prepend(startStat);
                    result = jcMethodDecl;
                }

            });
        });

        return true;
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

    private JCTree.JCMethodDecl makeGetterMethodDecl(JCTree.JCVariableDecl jcVariableDecl) {

        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        statements.append(treeMaker.Return(treeMaker.Select(treeMaker.Ident(names.fromString("this")), jcVariableDecl.getName())));
        JCTree.JCBlock body = treeMaker.Block(0, statements.toList());
        return treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), getNewMethodName(jcVariableDecl.getName()), jcVariableDecl.vartype, List.nil(), List.nil(), List.nil(), body, null);
    }

    private Name getNewMethodName(Name name) {
        String s = name.toString();
        return names.fromString("get" + s.substring(0, 1).toUpperCase() + s.substring(1, name.length()));
    }
}

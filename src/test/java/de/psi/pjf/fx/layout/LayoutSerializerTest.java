package de.psi.pjf.fx.layout;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.layout.Region;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.psi.pjf.fx.layout.container.ContainerAccessor;
import de.psi.pjf.fx.layout.container.ContainerConstants;
import de.psi.pjf.fx.layout.container.ContainerFactoryIf;
import de.psi.pjf.fx.layout.container.ContainerIf;
import de.psi.pjf.fx.layout.container.DndStackContainer;
import de.psi.pjf.fx.layout.container.LayoutContainerIf;
import de.psi.pjf.fx.layout.container.LayoutContainerImpl;
import de.psi.pjf.fx.layout.container.NodeContainerWrapper;
import de.psi.pjf.fx.layout.container.NodeCustomizerIf;
import de.psi.pjf.fx.layout.container.NodeCustomizerServiceIf;
import de.psi.pjf.fx.layout.container.NodeCustomizerServiceImpl;
import de.psi.pjf.fx.layout.container.SplitContainerIf;
import de.psi.pjf.fx.layout.container.SplitContainerImpl;
import de.psi.pjf.fx.layout.container.StackContainerIf;
import de.psi.pjf.fx.layout.container.StackContainerImpl;
import de.psi.pjf.fx.layout.container.TabContainerWrapperIf;
import de.psi.pjf.fx.layout.container.TabContainerWrapperImpl;
import de.psi.pjf.fx.layout.dnd.DefaultDndFeedback;
import de.psi.pjf.fx.layout.dnd.DndFeedbackService;
import de.psi.pjf.fx.layout.dnd.DndService;
import de.psi.pjf.fx.layout.dnd.DragData;
import de.psi.pjf.fx.layout.dnd.DropData;
import de.psi.pjf.fx.layout.profile.LayoutSerializer;
import de.psi.pjf.fx.layout.profile.LayoutSerializerIf;

public class LayoutSerializerTest
{
    private static final DndService dndService = Mockito.mock( DndService.class );
    private static final DndFeedbackService dndFeedbackService = new DefaultDndFeedback();
    public static final String SIMPLE_NODE_CUSTOMIZER_ID = "simpleNodeCustomizer";
    public static final NodeCustomizerIf SIMPLE_NODE_CUSTOMIZER = ( aContainer, aNode ) -> {
    };
    private final Consumer< DropData > testDropCallback = aDropData -> {
    };
    private final Predicate< DragData > testDragStartCallback = aDropData -> true;
    private LayoutSerializerIf serializer;
    private NodeCustomizerServiceIf nodeCustomizerService = new NodeCustomizerServiceImpl();

    @Before
    public void setUp() throws Exception
    {
        final ContainerFactoryIf containerFactory = Mockito.mock( ContainerFactoryIf.class );
        final ObjectMapper mapper = new ObjectMapper();
        final InjectableValues.Std values = new InjectableValues.Std();
        mapper.setInjectableValues( values );
        values.addValue( DndService.class, dndService );
        values.addValue( DndFeedbackService.class, dndFeedbackService );
        values.addValue( NodeCustomizerServiceIf.class, nodeCustomizerService );
        nodeCustomizerService.registerNodeCustomizer( SIMPLE_NODE_CUSTOMIZER_ID, SIMPLE_NODE_CUSTOMIZER );
        values.addValue( ContainerConstants.SPLIT_DROP_CALLBACK_NAME, testDropCallback );
        values.addValue( ContainerConstants.TAB_DRAG_START_CALLBACK_NAME, testDragStartCallback );
        values.addValue( ContainerConstants.TAB_DROP_CALLBACK_NAME, testDropCallback );
        Mockito.when( containerFactory.createObjectMapper() ).thenReturn( mapper );
        serializer = new LayoutSerializer( containerFactory );
    }

    @Test
    public void testSerialization() throws Exception
    {
        final DndStackContainer dndStackContainer = new DndStackContainer( dndService, dndFeedbackService );
        final SplitContainerIf< ? > split = new SplitContainerImpl();
        split.addChild( dndStackContainer );
        final LayoutContainerIf< ? > layoutContainer = new LayoutContainerImpl( split );
        final String jsonStr = serializer.toStringValue( layoutContainer );
        Assert.assertNotNull( jsonStr );
    }

    @Test
    public void testDeserialization() throws Exception
    {
        final DndStackContainer dndStackContainer = new DndStackContainer( dndService, dndFeedbackService );
        final SplitContainerIf< ? > split = new SplitContainerImpl();
        split.addChild( dndStackContainer );
        final LayoutContainerIf< ? > layoutContainer = new LayoutContainerImpl( split );
        final String jsonStr = serializer.toStringValue( layoutContainer );

        final LayoutContainerIf deserializedLayoutContainer = serializer.fromXml( jsonStr );
        Assert.assertNotNull( deserializedLayoutContainer );
        Assert.assertEquals( 1, deserializedLayoutContainer.getChildren().size() );
        final ContainerIf< ? > containerIf = deserializedLayoutContainer.getMainContainer();
        Assert.assertNotNull( containerIf );
        Assert.assertEquals( 1, containerIf.getChildren().size() );
        Assert.assertEquals( SplitContainerImpl.class, containerIf.getClass() );
        Assert.assertEquals( DndStackContainer.class, containerIf.getChildren().get( 0 ).getClass() );
    }

    @Test
    public void testTabsDeserialization() throws Exception
    {
        final StackContainerIf< ? > stackContainer = new StackContainerImpl();
        final NodeContainerWrapper screenWrapper = new NodeContainerWrapper( Region::new );
        final TabContainerWrapperIf< ? > tabContainerWrapper = new TabContainerWrapperImpl<>( screenWrapper );
        stackContainer.addChild( tabContainerWrapper );
        final SplitContainerIf< ? > split = new SplitContainerImpl();
        split.addChild( stackContainer );
        final LayoutContainerIf< ? > layoutContainer = new LayoutContainerImpl( split );
        final String jsonStr = serializer.toStringValue( layoutContainer );

        final LayoutContainerIf deserializedLayoutContainer = serializer.fromXml( jsonStr );
        final ContainerIf< ? > containerIf = deserializedLayoutContainer.getMainContainer();
        Assert.assertNotNull( containerIf );
        Assert.assertEquals( 1, containerIf.getChildren().size() );
        Assert.assertEquals( 1, containerIf.getChildren().get( 0 ).getChildren().size() );
        Assert.assertEquals( TabContainerWrapperImpl.class,
            containerIf.getChildren().get( 0 ).getChildren().get( 0 ).getClass() );
    }

    @Test
    public void testDropCallbackDeserialization() throws Exception
    {
        final DndStackContainer dndStackContainer = new DndStackContainer( dndService, dndFeedbackService );
        dndStackContainer.setSplitDropCallback( testDropCallback );
        dndStackContainer.setTabDropCallback( testDropCallback );
        dndStackContainer.setTabDragStartCallback( testDragStartCallback );
        final LayoutContainerIf< ? > layoutContainer = new LayoutContainerImpl( dndStackContainer );
        final String jsonStr = serializer.toStringValue( layoutContainer );
        final LayoutContainerIf deserializedLayoutContainer = serializer.fromXml( jsonStr );
        final ContainerIf mainContainer = deserializedLayoutContainer.getMainContainer();
        Assert.assertNotNull( mainContainer );
        final DndStackContainer dndDeserialized = (DndStackContainer)mainContainer;
        Assert.assertEquals( testDropCallback, dndDeserialized.getSplitDropCallback() );
        Assert.assertEquals( testDropCallback, dndDeserialized.getTabDropCallback() );
        Assert.assertEquals( testDragStartCallback, dndDeserialized.getTabDragStartCallback() );
    }

    @Test
    public void testNodeCustomizer() throws Exception
    {
        final DndStackContainer dndStackContainer = new DndStackContainer( dndService, dndFeedbackService );
        dndStackContainer.setSplitDropCallback( testDropCallback );
        dndStackContainer.setTabDropCallback( testDropCallback );
        dndStackContainer.setTabDragStartCallback( testDragStartCallback );
        dndStackContainer.setNodeCustomizerService( nodeCustomizerService );
        dndStackContainer.addNodeCustomizer( SIMPLE_NODE_CUSTOMIZER_ID );
        final LayoutContainerIf< ? > layoutContainer = new LayoutContainerImpl( dndStackContainer );
        final String jsonStr = serializer.toStringValue( layoutContainer );
        final LayoutContainerIf deserializedLayoutContainer = serializer.fromXml( jsonStr );
        final ContainerIf mainContainer = deserializedLayoutContainer.getMainContainer();
        Assert.assertNotNull( mainContainer );
        final DndStackContainer dndDeserialized = (DndStackContainer)mainContainer;
        final Set< String > dndContainerCustomizers = ContainerAccessor.getNodeCustomizers( dndDeserialized );
        Assert.assertTrue( dndContainerCustomizers.contains( SIMPLE_NODE_CUSTOMIZER_ID ) );
    }

    @Test
    public void testContainerIdMap() throws Exception
    {
        final DndStackContainer dndStackContainer = new DndStackContainer( dndService, dndFeedbackService );
        dndStackContainer.setSplitDropCallback( testDropCallback );
        dndStackContainer.setTabDropCallback( testDropCallback );
        dndStackContainer.setTabDragStartCallback( testDragStartCallback );
        dndStackContainer.setNodeCustomizerService( nodeCustomizerService );
        dndStackContainer.addNodeCustomizer( SIMPLE_NODE_CUSTOMIZER_ID );
        final LayoutContainerIf< ? > layoutContainer = new LayoutContainerImpl( dndStackContainer );
        final String dndStackStorageId = "DndStorageId555";
        layoutContainer.storeContainerId( dndStackStorageId, dndStackContainer );
        final String jsonStr = serializer.toStringValue( layoutContainer );
        final LayoutContainerIf deserializedLayoutContainer = serializer.fromXml( jsonStr );
        Assert.assertNotNull( deserializedLayoutContainer );
        Assert.assertNotNull( deserializedLayoutContainer.getContainerById( dndStackStorageId ) );
        Assert.assertEquals( deserializedLayoutContainer.getMainContainer(),
            deserializedLayoutContainer.getContainerById( dndStackStorageId ) );
    }
}
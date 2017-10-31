package com.qminder.instadownloader;

import com.qminder.instadownloader.service.PathResolverService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class,
        loader = AnnotationConfigContextLoader.class)
public class PathResolverServiceTest {

    @Autowired
    private PathResolverService pathResolverService;

    @Test
    public void testIsPathValid(){

        boolean result = pathResolverService.isPathValid("X://");
        assertFalse(result);
        result = pathResolverService.isPathValid("../");
        assertTrue(result);
    }
}

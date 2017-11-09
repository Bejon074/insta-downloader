package com.qminder.instadownloader;

import com.qminder.instadownloader.domain.RealTimeUserDetail;
import com.qminder.instadownloader.repository.UserRepository;
import com.qminder.instadownloader.service.ProfileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class,
        loader = AnnotationConfigContextLoader.class)
@DataJpaTest
public class ProfileServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileService profileService;

    @Test
    public void testFindByUserName() {
        RealTimeUserDetail userDetail = new RealTimeUserDetail();
        userDetail.setUserName("Test1");
        userDetail.setFileSavingDirectory("c://");
        userDetail.setMaxId("test1MaxId");
        userDetail.setFullName("Test1FullName");
        userRepository.saveAndFlush(userDetail);

        RealTimeUserDetail savedUSerDetail = profileService.getUserDetailByName("Test1");
        assertEquals("Test1", userDetail.getUserName());
        assertEquals("c://", userDetail.getFileSavingDirectory());
        assertEquals("test1MaxId", userDetail.getMaxId());
        assertEquals("Test1FullName", userDetail.getFullName());
    }

    @Test
    public void testSaveUserDetail() {
        RealTimeUserDetail userDetail = new RealTimeUserDetail();
        userDetail.setUserName("Test2");
        userDetail.setFileSavingDirectory("c://");
        userDetail.setMaxId("test2MaxId");
        userDetail.setFullName("Test2FullName");
        RealTimeUserDetail savedUserDetail1 = profileService.saveUserDetail(userDetail);

        assertEquals("Test2", savedUserDetail1.getUserName());
        assertEquals("c://", savedUserDetail1.getFileSavingDirectory());
        assertEquals("test2MaxId", savedUserDetail1.getMaxId());
        assertEquals("Test2FullName", savedUserDetail1.getFullName());
    }

    @Test
    public void testGetProfiles() {
        RealTimeUserDetail userDetail1 = new RealTimeUserDetail();
        userDetail1.setUserName("Test1");
        userDetail1.setFileSavingDirectory("c://");
        userDetail1.setMaxId("test1MaxId");
        userDetail1.setFullName("Test1FullName");
        userRepository.saveAndFlush(userDetail1);

        RealTimeUserDetail userDetail2 = new RealTimeUserDetail();
        userDetail2.setUserName("Test2");
        userDetail2.setFileSavingDirectory("c://");
        userDetail2.setMaxId("test2MaxId");
        userDetail2.setFullName("Test2FullName");
        userRepository.saveAndFlush(userDetail2);

        List<RealTimeUserDetail> userDetails = profileService.getProfiles();
        assertTrue(isContain(userDetail1, userDetails));
        assertTrue(isContain(userDetail2, userDetails));

    }

    private boolean isContain(RealTimeUserDetail userDetail, List<RealTimeUserDetail> userDetails) {
        for (RealTimeUserDetail savedUserDetail1 : userDetails) {
            if (savedUserDetail1.getUserName().equals(userDetail.getUserName()) &&
                    savedUserDetail1.getFullName().equals(userDetail.getFullName()) &&
                    savedUserDetail1.getFileSavingDirectory().equals(userDetail.getFileSavingDirectory()) &&
                    savedUserDetail1.getTotalFileDownloaded() == userDetail.getTotalFileDownloaded() &&
                    savedUserDetail1.getMaxId().equals(userDetail.getMaxId())) {
                return true;
            }
        }
        return false;
    }
}
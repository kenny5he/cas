package org.apereo.cas.authentication.principal.resolvers;

import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.handler.support.SimpleTestUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.DefaultPrincipalElectionStrategy;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.util.CollectionUtils;

import com.google.common.collect.Maps;
import lombok.val;
import org.apereo.services.persondir.IPersonAttributeDaoFilter;
import org.apereo.services.persondir.support.StubPersonAttributeDao;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test cases for {@link PersonDirectoryPrincipalResolver}.
 *
 * @author Misagh Moayyed
 * @since 4.2
 */
@SuppressWarnings("OptionalAssignedToNull")
@Tag("Simple")
public class PersonDirectoryPrincipalResolverTests {

    private static final String ATTR_1 = "attr1";

    @Test
    public void verifyOp() {
        val resolver = new PersonDirectoryPrincipalResolver(CoreAuthenticationTestUtils.getAttributeRepository(),
            PrincipalFactoryUtils.newPrincipalFactory(), true, "cn", true);
        val credential = mock(Credential.class);
        val p = resolver.resolve(credential, Optional.of(CoreAuthenticationTestUtils.getPrincipal()),
            Optional.of(new SimpleTestUsernamePasswordAuthenticationHandler()));
        assertNotNull(p);
    }

    @Test
    public void verifyOperation() {
        val resolver = new PersonDirectoryPrincipalResolver(CoreAuthenticationTestUtils.getAttributeRepository(),
            PrincipalFactoryUtils.newPrincipalFactory(), false, "cn", true, false);
        val credential = mock(Credential.class);
        val p = resolver.resolve(credential, Optional.of(CoreAuthenticationTestUtils.getPrincipal()),
            Optional.of(new SimpleTestUsernamePasswordAuthenticationHandler()));
        assertNotNull(p);
    }

    @Test
    public void verifyNullPrincipal() {
        val resolver = new PersonDirectoryPrincipalResolver();
        val p = resolver.resolve(() -> null, Optional.of(CoreAuthenticationTestUtils.getPrincipal()),
            Optional.of(new SimpleTestUsernamePasswordAuthenticationHandler()));
        assertNull(p);
    }

    @Test
    public void verifyNullAttributes() {
        val resolver = new PersonDirectoryPrincipalResolver(true, CoreAuthenticationTestUtils.CONST_USERNAME);
        val c = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword();
        val p = resolver.resolve(c, null);
        assertNull(p);
    }

    @Test
    public void verifyNullAttributeValues() {
        val attributes = new ArrayList<Object>();
        attributes.add(null);
        val resolver = new PersonDirectoryPrincipalResolver(
                new StubPersonAttributeDao(Map.of("a", attributes))
        );
        val principal = resolver.resolve((Credential) () -> "a");

        assertThat(principal.getAttributes()).containsExactly(Maps.immutableEntry("a", new ArrayList<>()));
    }

    @Test
    public void verifyNoAttributesWithPrincipal() {
        val resolver = new PersonDirectoryPrincipalResolver(CoreAuthenticationTestUtils.getAttributeRepository(),
            CoreAuthenticationTestUtils.CONST_USERNAME);
        val c = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword();
        val p = resolver.resolve(c, null);
        assertNotNull(p);
    }

    @Test
    public void verifyAttributesWithPrincipal() {
        val resolver = new PersonDirectoryPrincipalResolver(CoreAuthenticationTestUtils.getAttributeRepository(), "cn");
        val c = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword();
        val p = resolver.resolve(c, null);
        assertNotNull(p);
        assertNotEquals(p.getId(), CoreAuthenticationTestUtils.CONST_USERNAME);
        assertTrue(p.getAttributes().containsKey("memberOf"));
    }

    @Test
    public void verifyChainingResolverOverwrite() {
        val resolver = new PersonDirectoryPrincipalResolver(CoreAuthenticationTestUtils.getAttributeRepository());

        val chain = new ChainingPrincipalResolver(new DefaultPrincipalElectionStrategy());
        chain.setChain(Arrays.asList(new EchoingPrincipalResolver(), resolver));
        val attributes = new HashMap<String, List<Object>>();
        attributes.put("cn", List.of("originalCN"));
        attributes.put(ATTR_1, List.of("value1"));
        val p = chain.resolve(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword(),
            Optional.of(CoreAuthenticationTestUtils.getPrincipal(CoreAuthenticationTestUtils.CONST_USERNAME, attributes)),
            Optional.of(new SimpleTestUsernamePasswordAuthenticationHandler()));
        assertEquals(p.getAttributes().size(),
            CoreAuthenticationTestUtils.getAttributeRepository().getPossibleUserAttributeNames(IPersonAttributeDaoFilter.alwaysChoose()).size() + 1);
        assertTrue(p.getAttributes().containsKey(ATTR_1));
        assertTrue(p.getAttributes().containsKey("cn"));
        assertNotEquals("originalCN", p.getAttributes().get("cn"));
    }

    @Test
    public void verifyChainingResolver() {
        val resolver = new PersonDirectoryPrincipalResolver(CoreAuthenticationTestUtils.getAttributeRepository());

        val chain = new ChainingPrincipalResolver(new DefaultPrincipalElectionStrategy());
        chain.setChain(Arrays.asList(new EchoingPrincipalResolver(), resolver));
        val p = chain.resolve(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword(),
            Optional.of(CoreAuthenticationTestUtils.getPrincipal(CoreAuthenticationTestUtils.CONST_USERNAME,
                Collections.singletonMap(ATTR_1, List.of("value")))),
            Optional.of(new SimpleTestUsernamePasswordAuthenticationHandler()));
        assertEquals(p.getAttributes().size(),
            CoreAuthenticationTestUtils.getAttributeRepository().getPossibleUserAttributeNames(IPersonAttributeDaoFilter.alwaysChoose()).size() + 1);
        assertTrue(p.getAttributes().containsKey(ATTR_1));
    }

    @Test
    public void verifyChainingResolverOverwritePrincipal() {
        val resolver = new PersonDirectoryPrincipalResolver(CoreAuthenticationTestUtils.getAttributeRepository());
        val resolver2 = new PersonDirectoryPrincipalResolver(
            new StubPersonAttributeDao(Collections.singletonMap("principal",
                CollectionUtils.wrap("changedPrincipal"))), "principal");

        val chain = new ChainingPrincipalResolver(new DefaultPrincipalElectionStrategy());
        chain.setChain(Arrays.asList(new EchoingPrincipalResolver(), resolver, resolver2));

        val p = chain.resolve(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword(),
            Optional.of(CoreAuthenticationTestUtils.getPrincipal("somethingelse",
                Collections.singletonMap(ATTR_1, List.of("value")))),
            Optional.of(new SimpleTestUsernamePasswordAuthenticationHandler()));
        assertNotNull(p);
        assertEquals("changedPrincipal", p.getId());
        assertEquals(7, p.getAttributes().size());
        assertTrue(p.getAttributes().containsKey(ATTR_1));
        assertTrue(p.getAttributes().containsKey("principal"));
    }

    @Test
    public void verifyMultiplePrincipalAttributeNames() {
        val resolver = new PersonDirectoryPrincipalResolver(CoreAuthenticationTestUtils.getAttributeRepository());
        val resolver2 = new PersonDirectoryPrincipalResolver(
            new StubPersonAttributeDao(Collections.singletonMap("something", CollectionUtils.wrap("principal-id"))), " invalid, something");
        val chain = new ChainingPrincipalResolver(new DefaultPrincipalElectionStrategy());
        chain.setChain(Arrays.asList(new EchoingPrincipalResolver(), resolver, resolver2));

        val p = chain.resolve(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword(),
            Optional.of(CoreAuthenticationTestUtils.getPrincipal("somethingelse", Collections.singletonMap(ATTR_1, List.of("value")))),
            Optional.of(new SimpleTestUsernamePasswordAuthenticationHandler()));
        assertNotNull(p);
        assertEquals("principal-id", p.getId());
    }

    @Test
    public void verifyMultiplePrincipalAttributeNamesNotFound() {
        val resolver = new PersonDirectoryPrincipalResolver(CoreAuthenticationTestUtils.getAttributeRepository());
        val resolver2 = new PersonDirectoryPrincipalResolver(
            new StubPersonAttributeDao(Collections.singletonMap("something", CollectionUtils.wrap("principal-id"))), " invalid, ");
        val chain = new ChainingPrincipalResolver(new DefaultPrincipalElectionStrategy());
        chain.setChain(Arrays.asList(new EchoingPrincipalResolver(), resolver, resolver2));

        val p = chain.resolve(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword(),
            Optional.of(CoreAuthenticationTestUtils.getPrincipal("somethingelse", Collections.singletonMap(ATTR_1, List.of("value")))),
            Optional.of(new SimpleTestUsernamePasswordAuthenticationHandler()));
        assertNotNull(p);
        assertEquals("test", p.getId());
    }
}

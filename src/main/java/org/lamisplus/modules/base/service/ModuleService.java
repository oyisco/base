package org.lamisplus.modules.base.service;

import org.lamisplus.modules.base.domain.entities.Module;
import org.lamisplus.modules.base.domain.repositories.ModuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class ModuleService {
    private final ModuleRepository moduleRepository;
    private final TransactionTemplate transactionTemplate;

    public ModuleService(ModuleRepository moduleRepository, TransactionTemplate transactionTemplate) {
        this.moduleRepository = moduleRepository;
        this.transactionTemplate = transactionTemplate;
    }

    @Transactional
    public void deleteModule(Module module) {
        transactionTemplate.execute(ts -> {
            moduleRepository.deleteMenus(module.getId());
            moduleRepository.deleteDependency(module.getId());
            moduleRepository.deleteWebModule(module.getId());
            moduleRepository.deleteAuthorities(module.getId());
            moduleRepository.deleteViewTemplates(module.getId());
            moduleRepository.delete(module);
            return null;
        });
    }
}

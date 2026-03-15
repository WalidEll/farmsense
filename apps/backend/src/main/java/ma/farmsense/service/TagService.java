package ma.farmsense.service;

import lombok.RequiredArgsConstructor;
import ma.farmsense.dto.accounting.CreateTagRequest;
import ma.farmsense.dto.accounting.TagResponse;
import ma.farmsense.dto.accounting.UpdateTagRequest;
import ma.farmsense.entity.Tag;
import ma.farmsense.entity.User;
import ma.farmsense.exception.AppException;
import ma.farmsense.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public List<TagResponse> findAll(User user) {
        return tagRepository.findByUserOrderByNameAsc(user).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public TagResponse create(User user, CreateTagRequest req) {
        if (tagRepository.findByUserAndName(user, req.name()).isPresent()) {
            throw AppException.conflict("Tag with name '" + req.name() + "' already exists");
        }

        Tag tag = Tag.builder()
                .user(user)
                .name(req.name())
                .color(req.color())
                .build();

        return mapToResponse(tagRepository.save(tag));
    }

    @Transactional
    public TagResponse update(User user, UUID id, UpdateTagRequest req) {
        Tag tag = getOwnedTag(user, id);

        if (req.name() != null && !req.name().equals(tag.getName())) {
            if (tagRepository.findByUserAndName(user, req.name()).isPresent()) {
                throw AppException.conflict("Tag with name '" + req.name() + "' already exists");
            }
            tag.setName(req.name());
        }

        if (req.color() != null) {
            tag.setColor(req.color());
        }

        return mapToResponse(tagRepository.save(tag));
    }

    @Transactional
    public void delete(User user, UUID id) {
        Tag tag = getOwnedTag(user, id);
        tagRepository.delete(tag);
    }

    private Tag getOwnedTag(User user, UUID id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Tag not found"));
        if (!tag.getUser().getId().equals(user.getId())) {
            throw AppException.notFound("Tag not found");
        }
        return tag;
    }

    private TagResponse mapToResponse(Tag tag) {
        return new TagResponse(tag.getId(), tag.getName(), tag.getColor());
    }
}

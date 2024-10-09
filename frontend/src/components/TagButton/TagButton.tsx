import { XCircleIcon } from '@/assets/images';
import { Text } from '@/components';
import { theme } from '@/style/theme';
import { getTagColor } from '@/utils';

import * as S from './TagButton.style';

interface Props {
  id: number;
  name: string;
  isFocused?: boolean;
  disabled?: boolean;
  variant?: 'default' | 'edit';
  onClick?: () => void;
}

const TagButton = ({ id, name, isFocused = false, disabled = false, variant = 'default', onClick }: Props) => {
  const { background, border } = getTagColor(id);

  return (
    <S.TagButtonWrapper
      background={background}
      border={border}
      isFocused={isFocused}
      disabled={disabled}
      onClick={() => onClick && onClick()}
    >
      <Text.Medium color={theme.color.light.secondary_700}>{name}</Text.Medium>
      {variant === 'edit' && <XCircleIcon width={16} height={16} aria-label='태그 삭제' />}
    </S.TagButtonWrapper>
  );
};

export default TagButton;

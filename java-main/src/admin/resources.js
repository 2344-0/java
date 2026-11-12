// 后台管理 · 7 类首页数据模块的字段元数据
// 表格列 / 新增编辑表单都由这份配置自动生成，新增模块时只需在这里加一项。
// key 必须与后端 AdminDataController 注册表中的 type 一致。

const t = (key, label, extra) => Object.assign({ key, label, type: 'text' }, extra || {})
const num = (key, label) => ({ key, label, type: 'number' })
const area = (key, label, extra) => Object.assign({ key, label, type: 'textarea' }, extra || {})

export const RESOURCES = [
  {
    key: 'fires',
    label: '异火图鉴',
    icon: '火',
    desc: '首页「异火图鉴」卡片与异火榜的数据，含配图路径与搜索关键词。',
    primary: 'name',
    columns: [
      t('name', '异火名称', { required: true, width: 150 }),
      t('rankName', '榜单名次', { width: 150 }),
      t('level', '品阶描述', { width: 160 }),
      area('description', '异火简介', { width: 260 }),
      t('keywords', '搜索关键词', { width: 180 }),
      t('image', '配图路径', { width: 220 }),
      num('sort', '排序值')
    ]
  },
  {
    key: 'gongfa',
    label: '热门功法',
    icon: '诀',
    desc: '首页「热门功法」榜单，热度值会格式化为 9.8w 展示。',
    primary: 'name',
    columns: [
      t('name', '功法名称', { required: true, width: 180 }),
      area('description', '功法简介', { width: 320 }),
      num('hotValue', '热度值'),
      num('sort', '排序值')
    ]
  },
  {
    key: 'pills',
    label: '丹药坊',
    icon: '丹',
    desc: '丹药坊页面卡片：品阶、功效、主材与丹榜名次。',
    primary: 'name',
    columns: [
      t('name', '丹名', { required: true, width: 150 }),
      t('alias', '别称', { width: 130 }),
      t('grade', '品阶', { width: 130 }),
      area('effect', '功效', { width: 230 }),
      t('material', '主材料', { width: 180 }),
      area('description', '丹药简介', { width: 260 }),
      t('rankName', '丹榜名次', { width: 140 }),
      num('sort', '排序值')
    ]
  },
  {
    key: 'douqis',
    label: '斗气榜',
    icon: '斗',
    desc: '斗气榜的境界晋升顺序，排序值越小越靠前。',
    primary: 'name',
    columns: [
      t('name', '境界名称', { required: true, width: 140 }),
      t('tierDesc', '阶位特征', { width: 200 }),
      area('description', '境界简介', { width: 290 }),
      t('subLevel', '细分层级', { width: 150 }),
      num('sort', '晋升顺序')
    ]
  },
  {
    key: 'sects',
    label: '宗门势力',
    icon: '宗',
    desc: '宗门页面的势力榜单与掌权者信息。',
    primary: 'name',
    columns: [
      t('name', '势力名称', { required: true, width: 150 }),
      t('alias', '别称', { width: 130 }),
      t('grade', '势力等级', { width: 130 }),
      t('leader', '掌权者', { width: 130 }),
      area('description', '势力简介', { width: 280 }),
      t('rankName', '势力榜名次', { width: 150 }),
      num('sort', '排序值')
    ]
  },
  {
    key: 'hotwords',
    label: '热搜词',
    icon: '热',
    desc: '首页搜索框下方的「热门：」词条，按排序值升序展示。',
    primary: 'word',
    columns: [
      t('word', '热词内容', { required: true, width: 260 }),
      num('sort', '排序值')
    ]
  },
  {
    key: 'feed',
    label: '斗者动态',
    icon: '讯',
    desc: '首页「斗者动态」信息流，发布时间由数据库自动生成，按时间倒序展示。',
    primary: 'author',
    columns: [
      t('author', '发布者', { required: true, width: 150 }),
      area('content', '动态内容', { required: true, width: 420 }),
      t('createdAt', '发布时间', { type: 'readonly', width: 180 })
    ]
  }
]

export function findResource (key) {
  return RESOURCES.find(r => r.key === key) || null
}

/** 表单里可编辑的字段（排除只读列） */
export function editableColumns (resource) {
  return resource.columns.filter(c => c.type !== 'readonly')
}

/** 新建一条空表单数据 */
export function emptyForm (resource) {
  const form = {}
  editableColumns(resource).forEach(c => { form[c.key] = c.type === 'number' ? 0 : '' })
  return form
}

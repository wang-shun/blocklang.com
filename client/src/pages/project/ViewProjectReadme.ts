import * as css from './ViewProject.m.css';
import ThemedMixin, { theme } from '@dojo/framework/core/mixins/Themed';
import I18nMixin from '@dojo/framework/core/mixins/I18n';
import WidgetBase from '@dojo/framework/core/WidgetBase';
import { v } from '@dojo/framework/core/vdom';
import * as c from '../../className';

export interface ViewProjectReadmeProperties {}

@theme(css)
export default class ViewProjectReadme extends ThemedMixin(I18nMixin(WidgetBase))<ViewProjectReadmeProperties> {
	//private _localizedMessages = this.localizeBundle(messageBundle);

	protected render() {
		return v('div', { classes: [c.text_center, c.mt_5] }, [v('h1', ['项目 - README']), v('p', ['未开发'])]);
	}
}
